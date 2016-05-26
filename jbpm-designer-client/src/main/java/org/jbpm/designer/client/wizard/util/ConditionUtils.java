package org.jbpm.designer.client.wizard.util;

import org.jbpm.designer.model.Condition;
import org.jbpm.designer.model.Variable;

import java.util.ArrayList;
import java.util.List;

public class ConditionUtils {

    public static Condition constructReverseCondition(Condition oldCondition) {
        if(oldCondition != null) {
            Condition condition = new Condition();
            if(oldCondition.getConstraint() != null) {
                condition.setVariable(oldCondition.getVariable());
                condition.setConstraint(reverseConstraint(oldCondition.getConstraint()));
                condition.setConstraintValue(oldCondition.getConstraintValue());
            }
            return condition;
        }else {
            return null;
        }
    }

    private static String reverseConstraint(String constraint) {
        if(constraint != null) {
            if(constraint == Condition.IS_TRUE) {
                return Condition.IS_FALSE;
            } else if(constraint == Condition.IS_FALSE) {
                return Condition.IS_TRUE;
            } else if(constraint == Condition.EQUALS_TO) {
                return  Condition.NOT_EQUALS_TO;
            } else if(constraint == Condition.NOT_EQUALS_TO) {
                return Condition.EQUALS_TO;
            } else if(constraint == Condition.CONTAINS) {
                return Condition.NOT_CONTAINS;
            } else if(constraint == Condition.NOT_CONTAINS) {
                return Condition.CONTAINS;
            } else if(constraint == Condition.STARTS_WITH) {
                return Condition.NOT_STARTS_WITH;
            } else if(constraint == Condition.NOT_STARTS_WITH) {
                return Condition.STARTS_WITH;
            } else if(constraint == Condition.GREATER_THAN) {
                return Condition.EQUAL_OR_LESS_THAN;
            } else if(constraint == Condition.EQUAL_OR_LESS_THAN) {
                return Condition.GREATER_THAN;
            } else if(constraint == Condition.LESS_THAN) {
                return Condition.EQUAL_OR_GREATER_THAN;
            } else if(constraint == Condition.EQUAL_OR_GREATER_THAN) {
                return Condition.LESS_THAN;
            } else {
                return constraint;
            }
        } else {
            return null;
        }
    }

    public static List<String> getConstraints(Variable variable) {
        List<String> constraints = new ArrayList<String>();
        if(variable != null && variable.getDataType() != null) {
            if(variable.getDataType().compareTo("Float") == 0
                    || variable.getDataType().compareTo("Integer") == 0
                    || variable.getDataType().compareTo("Double") == 0) {
                constraints.add(Condition.LESS_THAN);
                constraints.add(Condition.EQUAL_OR_LESS_THAN);
                constraints.add(Condition.GREATER_THAN);
                constraints.add(Condition.EQUAL_OR_GREATER_THAN);
                constraints.add(Condition.EQUALS_TO);
                constraints.add(Condition.NOT_EQUALS_TO);
            } else if(variable.getDataType().compareTo("String") == 0) {
                constraints.add(Condition.CONTAINS);
                constraints.add(Condition.NOT_CONTAINS);
                constraints.add(Condition.STARTS_WITH);
                constraints.add(Condition.NOT_STARTS_WITH);
                constraints.add(Condition.EQUALS_TO);
                constraints.add(Condition.NOT_EQUALS_TO);
            } else if(variable.getDataType().compareTo("Boolean") == 0) {
                constraints.add(Condition.IS_TRUE);
                constraints.add(Condition.IS_FALSE);
            }
        }
        return constraints;
    }
}
