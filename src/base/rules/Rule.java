package base.rules;

import base.variables.Variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rule implements Serializable {
    private UUID guid;
    private String name;
    private String comment;

    private List<Fact> premises;
    private List<Variable> needs;
    private List<Fact> conclusions;
    private List<Variable> changes;

    private int priority;
    private int cost;
    private String reason;

    public Rule(UUID guid, String name){
        this.guid = guid;
        this.name = name;

        premises = new ArrayList<>();
        needs = new ArrayList<>();
        conclusions = new ArrayList<>();
        changes = new ArrayList<>();
    }

    public UUID getGuid() {
        return guid;
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
    public List<Fact> getPremises() {
        return premises;
    }
    public List<Fact> getConclusions() {
        return conclusions;
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

    @Override
    public String toString(){ return name; }
}
