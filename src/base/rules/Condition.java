package base.rules;

import base.exceptions.OutOfDomainException;
import base.variables.Variable;

public class Condition<T extends Comparable<T>> {
    private Variable<T> checkedVariable;
    private T desiredValue;

    public Condition(Variable<T> checkedVariable, T desiredValue) {
        this.checkedVariable = checkedVariable;
        this.desiredValue = desiredValue;
    }

    public Variable getCheckedVariable() {
        return checkedVariable;
    }

    public void setCheckedVariable(Variable<T> checkedVariable) {
        this.checkedVariable = checkedVariable;
    }

    public T getDesiredValue() {
        return desiredValue;
    }

    public void setDesiredValue(T desiredValue) throws OutOfDomainException {
        if (!checkedVariable.getDomain().contains(desiredValue))
            throw new OutOfDomainException();
        this.desiredValue = desiredValue;
    }

    public boolean check() {
        return checkedVariable.getValue().equals(desiredValue);
    }
}