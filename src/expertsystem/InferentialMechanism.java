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
    private Supplier<ReasoningSubsystem> reasoningSubsystemConnector;
    private Supplier<KnowledgeBase> knowledgeBaseConnector;
    private Supplier<ScratchStorage> scratchStorageSupplier;

    private Value deduceGoal(Variable goal, VariableReasoningNode rnode) {
        Value value = null;
        for (Rule rule : knowledgeBaseConnector.get().getRules()){
            if (scratchStorageSupplier.get().ruleIsActivated(rule) || rule.getConclusions().stream().noneMatch(fact -> fact.getVariable().getGuid().equals(goal.getGuid())))
                continue;
            RuleReasoningNode ruleNode = new RuleReasoningNode(rule, rnode);
            boolean activated = true;
            for (Fact premise : rule.getPremises()) {
                Variable lookedUpVar = premise.getVariable();
                VariableReasoningNode ruleVarNode =
                        scratchStorageSupplier.get().variableIsUsed(lookedUpVar) ?
                        reasoningSubsystemConnector.get().getVarReasoning(lookedUpVar) :
                        new VariableReasoningNode(lookedUpVar, ruleNode);
                Value val = evaluateGoal(premise.getVariable(), ruleVarNode);
                if (premise.getAssignable() instanceof Value)
                    activated = val.getGuid().equals(premise.getAssignable().getGuid());
                else
                    if (premise.getAssignable() instanceof Variable)
                        activated = val.getGuid().equals(
                                evaluateGoal((Variable) premise.getAssignable(),
                                    new VariableReasoningNode((Variable) premise.getAssignable(), ruleNode)).getGuid());
                if (!activated)
                    break;
                ruleNode.getLookedUpVariables().add(ruleVarNode);
             }
            if (activated) {
                rnode.setActivatedRule(ruleNode);
                scratchStorageSupplier.get().activateRule(rule);
                value = evaluateGoal(goal, rnode);
                break;
            }
        }
        return value;
    }

    Value evaluatRootGoal(Variable goal) {
        ReasoningTree reasoningTree = reasoningSubsystemConnector.get().initReasoning(goal);
        return evaluateGoal(goal, reasoningTree.getRoot());
    }

    private Value evaluateGoal(Variable goal, VariableReasoningNode rnode) {
        if (scratchStorageSupplier.get().variableIsUsed(goal)){
            rnode.setAssociatedFact(scratchStorageSupplier.get().getAssociatedFact(goal));
            return scratchStorageSupplier.get().getVariableValue(goal);
        }
        Value value = null;
        if (!scratchStorageSupplier.get().variableIsUsed(goal))
            reasoningSubsystemConnector.get().addReasoning(goal, rnode);
        switch (goal.getVarClass()) {
            case REQUESTED: {
                value =  questioner.apply(goal);
                tryCreateNewFact(goal, value, rnode);
                break;
            }
            case DEDUCTED:
                value =  deduceGoal(goal, rnode);
                break;
            case REQUESTED_DEDUCTED: {
                value = questioner.apply(goal);
                if (value == null)
                    value = deduceGoal(goal, rnode);
                else
                    tryCreateNewFact(goal, value, rnode);
                break;
            }
            case DEDUCTED_REQUESTED:
                value = deduceGoal(goal, rnode);
                if (value == null) {
                    value = questioner.apply(goal);
                    tryCreateNewFact(goal, value, rnode);
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

    private void tryCreateNewFact(Variable variable, Value value, VariableReasoningNode rnode) {
        if (value != null) {
            Fact newFact = new Fact();
            newFact.setVariable(variable);
            newFact.setAssignable(value);
            rnode.setAssociatedFact(newFact);
            scratchStorageSupplier.get().useFact(newFact);
        }
    }

    public Supplier<ReasoningSubsystem> getReasoningSubsystemConnector() {
        return reasoningSubsystemConnector;
    }

    void setReasoningSubsystemConnector(Supplier<ReasoningSubsystem> reasoningSubsystemConnector) {
        this.reasoningSubsystemConnector = reasoningSubsystemConnector;
    }
}
