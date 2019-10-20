package expertsystem;

import base.domains.Value;
import base.knowledgebase.KnowledgeBase;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Variable;

import java.util.function.Function;
import java.util.function.Supplier;

class InferentialMechanism {
    private Function<Variable, Value> questioner;
    private Supplier<KnowledgeBase> knowledgeBaseConnector;
    private Supplier<ScratchStorage> scratchStorageSupplier;

    private Value deduceGoal(Variable goal) {
        Value value = null;
        for (Rule rule : knowledgeBaseConnector.get().getRules())
            if (rule.getConclusions().stream().anyMatch(fact -> fact.getVariable().getGuid().equals(goal.getGuid())))
            {
                boolean activated = true;
                for (Fact premise : rule.getPremises()) {
                    Value val = evaluateGoal(premise.getVariable());
                    if (premise.getAssignable() instanceof Value)
                        activated = val.getGuid().equals(premise.getAssignable().getGuid());
                    else
                        if (premise.getAssignable() instanceof Variable)
                            activated = val.getGuid().equals(
                                    evaluateGoal((Variable) premise.getAssignable()).getGuid());
                    if (!activated)
                        break;
                }
                if (activated) {
                    rule.getConclusions().forEach(conclusion -> scratchStorageSupplier.get().useFact(conclusion));
                    value = evaluateGoal(goal);
                    break;
                }
            }
        return value;
    }

    Value evaluateGoal(Variable goal) {
        if (scratchStorageSupplier.get().variableIsUsed(goal))
            return scratchStorageSupplier.get().getVariableValue(goal);
        Value value = null;
        switch (goal.getVarClass()) {
            case REQUESTED: {
                value =  questioner.apply(goal);
                tryCreateNewFact(goal, value);
                break;
            }
            case DEDUCTED:
                value =  deduceGoal(goal);
                break;
            case REQUESTED_DEDUCTED: {
                value = questioner.apply(goal);
                if (value == null)
                    value = deduceGoal(goal);
                else
                    tryCreateNewFact(goal, value);
                break;
            }
            case DEDUCTED_REQUESTED:
                value = deduceGoal(goal);
                if (value == null) {
                    value = questioner.apply(goal);
                    tryCreateNewFact(goal, value);
                }
                break;
        }
        return value;
    }

    Supplier<KnowledgeBase> getKnowledgeBaseConnector() {
        return knowledgeBaseConnector;
    }

    void setKnowledgeBaseConnector(Supplier<KnowledgeBase> knowledgeBaseConnector) {
        this.knowledgeBaseConnector = knowledgeBaseConnector;
    }

    Supplier<ScratchStorage> getScratchStorageSupplier() {
        return scratchStorageSupplier;
    }

    void setScratchStorageSupplier(Supplier<ScratchStorage> scratchStorageSupplier) {
        this.scratchStorageSupplier = scratchStorageSupplier;
    }

    Function<Variable, Value> getQuestioner() {
        return questioner;
    }

    void setQuestioner(Function<Variable, Value> questioner) {
        this.questioner = questioner;
    }

    private void tryCreateNewFact(Variable variable, Value value) {
        if (value != null) {
            Fact newFact = new Fact();
            newFact.setVariable(variable);
            newFact.setAssignable(value);
            scratchStorageSupplier.get().useFact(newFact);
        }
    }
}
