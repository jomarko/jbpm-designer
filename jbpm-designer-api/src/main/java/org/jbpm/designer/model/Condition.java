package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class Condition {

    public static String IS_TRUE = "is true";
    public static String IS_FALSE = "is false";
    public static String EQUALS_TO = "equals to";
    public static String NOT_EQUALS_TO = "not equals to";
    public static String CONTAINS = "contains";
    public static String NOT_CONTAINS = "not contains";
    public static String STARTS_WITH = "starts with";
    public static String NOT_STARTS_WITH = "not starts with";
    public static String GREATER_THAN = "greater than";
    public static String LESS_THAN = "less than";
    public static String EQUAL_OR_GREATER_THAN = "equal or greater than";
    public static String EQUAL_OR_LESS_THAN = "equal or less than";

    private Variable variable;

    private String constraint;

    private String constraintValue;

    public Condition() {
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
        if (!(o instanceof Condition)) return false;

        Condition condition = (Condition) o;

        if (getVariable() != null ? !getVariable().equals(condition.getVariable()) : condition.getVariable() != null)
            return false;
        if (getConstraint() != null ? !getConstraint().equals(condition.getConstraint()) : condition.getConstraint() != null)
            return false;
        return getConstraintValue() != null ? getConstraintValue().equals(condition.getConstraintValue()) : condition.getConstraintValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getVariable() != null ? getVariable().hashCode() : 0;
        result = 31 * result + (getConstraint() != null ? getConstraint().hashCode() : 0);
        result = 31 * result + (getConstraintValue() != null ? getConstraintValue().hashCode() : 0);
        return result;
    }
}
