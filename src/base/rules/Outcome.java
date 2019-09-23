package base.rules;

import base.variables.Variable;

import java.util.function.Supplier;

public class Outcome<T extends Comparable<T>> {
    private Variable<T> targetVariable;
    private Supplier<T> assignedValue;

    public Outcome(Variable<T> targetVariable, Supplier<T> assignedValue) {
        this.targetVariable = targetVariable;
        this.assignedValue = assignedValue;
    }

    public Variable<T> getTargetVariable() {
        return targetVariable;
    }

    public void setTargetVariable(Variable<T> targetVariable) {
        this.targetVariable = targetVariable;
    }

    public Supplier<T> getAssignedValue() {
        return assignedValue;
    }

    public void setAssignedValue(Supplier<T> assignedValue) {
        this.assignedValue = assignedValue;
    }
}
