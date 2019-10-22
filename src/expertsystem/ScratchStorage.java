package expertsystem;

import base.domains.Value;
import base.rules.Assignable;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Variable;

import java.util.*;

class ScratchStorage {
    private List<Fact> usedFacts;
    private Map<UUID, Integer> varIndices;
    private Set<UUID> activatedRules;

    ScratchStorage() {
        usedFacts = new ArrayList<>();
        varIndices = new HashMap<>();
        activatedRules = new HashSet<>();
    }

    void useFact(Fact fact) {
        varIndices.put(fact.getVariable().getGuid(), usedFacts.size());
        usedFacts.add(fact);
    }

    boolean variableIsUsed(Variable variable) {
        return varIndices.containsKey(variable.getGuid());
    }

    boolean ruleIsActivated(Rule rule) {
        return activatedRules.contains(rule.getGuid());
    }

    void activateRule(Rule rule) {
        activatedRules.add(rule.getGuid());
        rule.getConclusions().forEach(this::useFact);
    }

    Fact getAssociatedFact(Variable variable) {
        return usedFacts.get(varIndices.get(variable.getGuid()));
    }

    Value getVariableValue(Variable variable) {
        Assignable value = usedFacts.get(varIndices.get(variable.getGuid())).getAssignable();
        while (value != null && !(value instanceof Value))
            value = varIndices.containsKey(value.getGuid()) ?
            usedFacts.get(varIndices.get(value.getGuid())).getAssignable() : null;
        return (Value) value;
    }

    List<Fact> getUsedFacts() { return new ArrayList<>(usedFacts); }

    boolean isEmpty() {
        return usedFacts.isEmpty() && activatedRules.isEmpty();
    }

    void clear() {
        usedFacts.clear();
        varIndices.clear();
        activatedRules.clear();
    }
}
