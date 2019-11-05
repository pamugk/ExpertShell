package expertsystem;

import base.variables.Variable;

public class ReasoningTree {
    private VariableReasoningNode root;

    public ReasoningTree(Variable goal) {
        root = new VariableReasoningNode(goal, null);
    }

    public VariableReasoningNode getRoot() {
        return root;
    }
}
