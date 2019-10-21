package expertsystem;

import base.variables.Variable;

class ReasoningSubsystem {
    private ReasoningTree reasoningTree;

    ReasoningTree initReasoning(Variable goal) {
        reasoningTree = new ReasoningTree(goal);
        return reasoningTree;
    }

    ReasoningTree getReasoning() {
        return reasoningTree;
    }

    void clear() {
        reasoningTree = null;
    }
}
