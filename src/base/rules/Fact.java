package base.rules;

import base.domains.Value;
import base.variables.Variable;

import java.io.Serializable;

public class Fact implements Serializable {
    private Variable variable;
    private Value assignedValue;
    private boolean assignedValueOfVariable;
    private Variable assignedVariable;

    public Variable getVariable() {
        return variable;
    }
    public void setVariable(Variable variable) {
        this.variable = variable;
    }
    public Value getAssignedValue() {
        return assignedValue;
    }
    public void setAssignedValue(Value assignedValue) {
        this.assignedValue = assignedValue;
    }
    public Variable getAssignedVariable() {
        return assignedVariable;
    }
    public void setAssignedVariable(Variable assignedVariable) {
        this.assignedVariable = assignedVariable;
    }
    public boolean isAssignedValueOfVariable() {
        return assignedValueOfVariable;
    }

    public void setAssignedValueOfVariable(boolean assignedValueOfVariable) {
        this.assignedValueOfVariable = assignedValueOfVariable;
    }
}
