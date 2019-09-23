package base.rules;

import base.variables.Variable;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private String name;
    private String comment;

    private List<Condition> conditions;
    private List<Variable> needs;
    private List<Outcome> outcomes;
    private List<Variable> changes;

    private int priority;
    private int cost;
    private String reason;

    public Rule(String name){
        this.name = name;
        conditions = new ArrayList<>();
        needs = new ArrayList<>();
        outcomes = new ArrayList<>();
        changes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Outcome> getOutcomes() {
        return outcomes;
    }

    public List<Variable> getNeeds() {
        return needs;
    }

    public List<Variable> getChanges() {
        return changes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
