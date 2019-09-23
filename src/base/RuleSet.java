package base;

import base.domains.Domain;
import base.rules.Rule;
import base.variables.Variable;

import java.util.ArrayList;
import java.util.List;

public class RuleSet {
    private Variable goal;
    private List<Domain> usedDomains;
    private List<Variable> variables;
    private List<Rule> rules;

    public RuleSet() {
        usedDomains = new ArrayList<>();
        rules = new ArrayList<>();
        variables = new ArrayList<>();
    }

    public boolean goalIsDefined() {
        return goal != null;
    }

    public Variable getGoal() {
        return goal;
    }

    public void setGoal(Variable goal) {
        this.goal = goal;
    }

    public List<Domain> getUsedDomains() {
        return usedDomains;
    }

    public List<Variable> variables() {
        return variables;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
