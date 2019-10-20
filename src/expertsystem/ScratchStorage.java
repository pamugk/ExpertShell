package expertsystem;

import base.domains.Value;
import base.rules.Assignable;
import base.rules.Fact;
import base.rules.Rule;
import base.variables.Variable;

import java.util.*;

class ScratchStorage {
    private Map<UUID, Fact> usedFacts;
    private TraverseTree<Rule> activatedRulesTree;

    ScratchStorage() {
        usedFacts = new HashMap<>();
        activatedRulesTree = new TraverseTree<>();
    }

    void useFact(Fact fact) {
        usedFacts.put(fact.getVariable().getGuid(), fact);
    }

    boolean variableIsUsed(Variable variable) {
        return usedFacts.containsKey(variable.getGuid());
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

    TraverseTree<Rule> getActivatedRulesTree() {
        return activatedRulesTree;
    }

    void clear() {
        usedFacts.clear();
        activatedRulesTree.clear();
    }
}
