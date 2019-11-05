package expertsystem;

import base.rules.Fact;
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
        return associatedRule.getContent();
    }

    @Override
    public String toString() {
        return associatedRule.getName();
    }

    public List<Fact> collectAssociatedFacts() {
        List<Fact> associatedFacts = new ArrayList<>();
        associatedFacts.add(asociatedVar.getAssociatedFact());
        fillAssociatedFacts(associatedFacts);
        return associatedFacts;
    }

    private void fillAssociatedFacts(List<Fact> associatedFacts) {
        lookedUpVariables.forEach(variable -> {
            associatedFacts.add(variable.getAssociatedFact());
            if (variable.getActivatedRule() != null)
                variable.getActivatedRule().fillAssociatedFacts(associatedFacts);
        });
    }
}
