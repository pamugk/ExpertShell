package base.knowledgebase;

import base.domains.Domain;
import base.rules.Rule;
import base.environmentvars.EnvironmentVariablesSet;
import base.variables.Variable;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {
    private String name;

    private Variable goal;
    private List<Domain> usedDomains;
    private List<Variable> variables;
    private List<Rule> rules;
    private EnvironmentVariablesSet deductionSettings;

    public KnowledgeBase(String name) {
        this.name = name;
        usedDomains = new ArrayList<>();
        rules = new ArrayList<>();
        variables = new ArrayList<>();
        deductionSettings = new EnvironmentVariablesSet();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public EnvironmentVariablesSet getDeductionSettings() {
        return deductionSettings;
    }
}
