package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class Constraint {

    public static String EQUAL_TO = "equal to";
    public static String CONTAINS = "contains";
    public static String STARTS_WITH = "starts with";
    public static String GREATER_THAN = "greater than";
    public static String LESS_THAN = "less than";
    public static String EQUAL_OR_GREATER_THAN = "equal or greater than";
    public static String EQUAL_OR_LESS_THAN = "equal or less than";

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
