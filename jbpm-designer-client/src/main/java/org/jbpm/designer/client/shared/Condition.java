package org.jbpm.designer.client.shared;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class Condition {
    private Constraint constraint;

    private boolean executeIfConstraintSatisfied;

    public Condition() {
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public boolean isExecuteIfConstraintSatisfied() {
        return executeIfConstraintSatisfied;
    }

    public void setExecuteIfConstraintSatisfied(boolean executeIfConstraintSatisfied) {
        this.executeIfConstraintSatisfied = executeIfConstraintSatisfied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Condition)) return false;

        Condition condition = (Condition) o;

        if (isExecuteIfConstraintSatisfied() != condition.isExecuteIfConstraintSatisfied()) return false;
        return getConstraint() != null ? getConstraint().equals(condition.getConstraint()) : condition.getConstraint() == null;

    }

    @Override
    public int hashCode() {
        int result = getConstraint() != null ? getConstraint().hashCode() : 0;
        result = 31 * result + (isExecuteIfConstraintSatisfied() ? 1 : 0);
        return result;
    }
}
