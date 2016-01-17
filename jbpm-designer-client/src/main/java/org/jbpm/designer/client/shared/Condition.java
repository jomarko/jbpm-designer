package org.jbpm.designer.client.shared;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class Condition {
    private Constraint constraint;

    private int positiveTaskId;

    private int negativeTaskId;

    public Condition() {
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public int getPositiveTaskId() {
        return positiveTaskId;
    }

    public void setPositiveTaskId(int positiveTaskId) {
        this.positiveTaskId = positiveTaskId;
    }

    public int getNegativeTaskId() {
        return negativeTaskId;
    }

    public void setNegativeTaskId(int negativeTaskId) {
        this.negativeTaskId = negativeTaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Condition)) return false;

        Condition condition = (Condition) o;

        if (getPositiveTaskId() != condition.getPositiveTaskId()) return false;
        if (getNegativeTaskId() != condition.getNegativeTaskId()) return false;
        return getConstraint() != null ? getConstraint().equals(condition.getConstraint()) : condition.getConstraint() == null;

    }

    @Override
    public int hashCode() {
        int result = getConstraint() != null ? getConstraint().hashCode() : 0;
        result = 31 * result + getPositiveTaskId();
        result = 31 * result + getNegativeTaskId();
        return result;
    }
}
