package expertsystem;

import base.rules.Fact;
import base.variables.Variable;

public class VariableReasoningNode extends ReasoningTreeNode {
    private Variable associatedVariable;
    private Fact associatedFact;
    private RuleReasoningNode associatedRule;
    private RuleReasoningNode activatedRule;

    VariableReasoningNode(Variable variable, RuleReasoningNode associatedRule) {
        associatedVariable = variable;
        this.associatedRule = associatedRule;
    }

    public RuleReasoningNode getActivatedRule() {
        return activatedRule;
    }

    public void setActivatedRule(RuleReasoningNode activatedRule){
        this.activatedRule = activatedRule;
    }

    public RuleReasoningNode getAssociatedRule() {
        return associatedRule;
    }

    public Variable getAssociatedVariable() {
        return associatedVariable;
    }

    public Fact getAssociatedFact() {
        return associatedFact;
    }

    public void setAssociatedFact(Fact associatedFact) {
        this.associatedFact = associatedFact;
    }

    @Override
    public String getDescription() {
        return toString();
    }

    @Override
    public String toString() {
        return String.format("%s = %s", associatedVariable,
                associatedFact == null ? "?" : String.format("'%s'", associatedFact.getAssignable()));
    }
}
