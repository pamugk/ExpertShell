package base.rules;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private int idx;
    private int priority;
    private int cost;
    private String name;

    private List<Condition> conditions;
    private Outcome outcome;

    private String reason;

    public Rule(int idx, String name, List<Condition> conditions){
        this.idx = idx;
        this.name = name;
        this.conditions = new ArrayList<>();
        this.conditions.addAll(conditions);
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setOutcome(Outcome outcome) {
        if (outcome == null)
            throw new NullPointerException();
        this.outcome = outcome;
    }
}
