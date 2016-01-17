package org.jbpm.designer.client.shared;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class Constraint {
    private Variable variable;

    private String constraint;

    private String constraintValue;

    public Constraint() {
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getConstraintValue() {
        return constraintValue;
    }

    public void setConstraintValue(String constraintValue) {
        this.constraintValue = constraintValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constraint)) return false;

        Constraint that = (Constraint) o;

        if (getVariable() != null ? !getVariable().equals(that.getVariable()) : that.getVariable() != null)
            return false;
        if (getConstraint() != null ? !getConstraint().equals(that.getConstraint()) : that.getConstraint() != null)
            return false;
        return getConstraintValue() != null ? getConstraintValue().equals(that.getConstraintValue()) : that.getConstraintValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getVariable() != null ? getVariable().hashCode() : 0;
        result = 31 * result + (getConstraint() != null ? getConstraint().hashCode() : 0);
        result = 31 * result + (getConstraintValue() != null ? getConstraintValue().hashCode() : 0);
        return result;
    }
}
