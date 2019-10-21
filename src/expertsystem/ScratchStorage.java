package expertsystem;

import base.domains.Value;
import base.rules.Assignable;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Variable;

import java.util.*;

class ScratchStorage {
    private Map<UUID, Fact> usedFacts;
    private Set<UUID> activatedRules;

    ScratchStorage() {
        usedFacts = new HashMap<>();
        activatedRules = new HashSet<>();
    }

    void useFact(Fact fact) {
        usedFacts.put(fact.getVariable().getGuid(), fact);
    }

    boolean variableIsUsed(Variable variable) {
        return usedFacts.containsKey(variable.getGuid());
    }

    boolean ruleIsActivated(Rule rule) {
        return activatedRules.contains(rule.getGuid());
    }

    void activateRule(Rule rule) {
        activatedRules.add(rule.getGuid());
        rule.getConclusions().forEach(this::useFact);
    }

    Fact getAssociatedFact(Variable variable) {
        return usedFacts.get(variable.getGuid());
    }

    Value getVariableValue(Variable variable) {
        Assignable value = usedFacts.get(variable.getGuid()).getAssignable();
        while (value != null && !(value instanceof Value))
            value = usedFacts.containsKey(value.getGuid()) ? usedFacts.get(value.getGuid()).getAssignable() : null;
        return (Value) value;
    }

    List<Fact> getUsedFacts() {
        return new ArrayList<>(usedFacts.values());
    }

    boolean isEmpty() {
        return usedFacts.isEmpty() && activatedRules.isEmpty();
    }

    void clear() {
        usedFacts.clear();
        activatedRules.clear();
    }
}
