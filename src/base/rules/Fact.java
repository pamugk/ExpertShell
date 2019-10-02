package base.rules;

import base.variables.Variable;

import java.io.Serializable;

public class Fact implements Serializable {
    private Variable variable;
    private Assignable assignable;

    public Variable getVariable() {
        return variable;
    }
    public void setVariable(Variable variable) {
        this.variable = variable;
    }
    public Assignable getAssignable() { return assignable; }
    public void setAssignable(Assignable assignable) { this.assignable = assignable; }
}
