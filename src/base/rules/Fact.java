package base.rules;

import base.variables.Variable;

import java.io.Serializable;

public class Fact implements Serializable {
    private Variable variable;
    private String assignedValue;
    private boolean assignedValueOfVariable;

    public Variable getVariable() {
        return variable;
    }
    public void setVariable(Variable variable) {
        this.variable = variable;
    }
    public String getAssignedValue() {
        return assignedValue;
    }
    public void setAssignedValue(String assignedValue) {
        this.assignedValue = assignedValue;
    }
    public boolean isAssignedValueOfVariable() {
        return assignedValueOfVariable;
    }

    public void setAssignedValueOfVariable(boolean assignedValueOfVariable) {
        this.assignedValueOfVariable = assignedValueOfVariable;
    }
}
