package expertsystem;

import base.rules.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleReasoningNode extends ReasoningTreeNode {
    private Rule associatedRule;
    private VariableReasoningNode asociatedVar;
    private List<VariableReasoningNode> lookedUpVariables;

    RuleReasoningNode(Rule rule, VariableReasoningNode asociatedVar) {
        associatedRule = rule;
        this.asociatedVar = asociatedVar;
        lookedUpVariables = new ArrayList<>();
    }

    public VariableReasoningNode getAsociatedVar() {
        return asociatedVar;
    }

    public List<VariableReasoningNode> getLookedUpVariables() {
        return lookedUpVariables;
    }

    public Rule getAssociatedRule() {
        return associatedRule;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String toString() {
        return associatedRule.getName();
    }
}
