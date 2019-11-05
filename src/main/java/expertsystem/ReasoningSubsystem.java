package expertsystem;

import base.variables.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class ReasoningSubsystem {
    private ReasoningTree reasoningTree;
    private Map<UUID, VariableReasoningNode> varReasoning;

    ReasoningTree initReasoning(Variable goal) {
        reasoningTree = new ReasoningTree(goal);
        varReasoning = new HashMap<>();
        return reasoningTree;
    }

    ReasoningTree getReasoning() {
        return reasoningTree;
    }

    void addReasoning(Variable variable, VariableReasoningNode reasoning) {
        varReasoning.put(variable.getGuid(), reasoning);
    }

    VariableReasoningNode getVarReasoning(Variable variable) {
        return varReasoning.get(variable.getGuid());
    }

    void clear() {
        reasoningTree = null;
        if (varReasoning != null)
            varReasoning.clear();
    }
}
