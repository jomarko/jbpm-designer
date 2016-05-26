package org.jbpm.designer.server.service.util;

import org.jbpm.designer.model.Condition;

public class KieFunctionsGenerator {
    public static String getKieFunctionsExpression(Condition condition) {
        String expressionBody = "";
        if(condition != null && condition.getConstraint() != null && condition.getVariable() != null) {
            if(!condition.getConstraint().startsWith("not")){
                expressionBody += "return KieFunctions.";
            } else {
                expressionBody += "return !KieFunctions.";
            }
            String constraint = condition.getConstraint();
            if (constraint.compareTo(Condition.EQUALS_TO) == 0 || constraint.compareTo(Condition.NOT_EQUALS_TO) == 0) {
                expressionBody += "equalsTo(";
            } else if (constraint.compareTo(Condition.STARTS_WITH) == 0 || constraint.compareTo(Condition.NOT_STARTS_WITH) == 0) {
                expressionBody += "startsWith(";
            } else if (constraint.compareTo(Condition.CONTAINS) == 0 || constraint.compareTo(Condition.NOT_CONTAINS) == 0) {
                expressionBody += "contains(";
            }else if (constraint.compareTo(Condition.GREATER_THAN) == 0) {
                expressionBody += "greaterThan(";
            } else if (constraint.compareTo(Condition.EQUAL_OR_GREATER_THAN) == 0) {
                expressionBody += "greaterOrEqualThan(";
            } else if (constraint.compareTo(Condition.LESS_THAN) == 0) {
                expressionBody += "lessThan(";
            } else if (constraint.compareTo(Condition.EQUAL_OR_LESS_THAN) == 0) {
                expressionBody += "lessOrEqualThan(";
            } else if (constraint.compareTo(Condition.IS_TRUE) == 0) {
                expressionBody += "isTrue(" + condition.getVariable().getName() + ");";
            } else if (constraint.compareTo(Condition.IS_FALSE) == 0) {
                expressionBody += "isFalse(" + condition.getVariable().getName() + ");";
            }

            if(!expressionBody.endsWith(");")) {
                expressionBody += condition.getVariable().getName();
                expressionBody += ",\"";
                expressionBody += condition.getConstraintValue();
                expressionBody += "\");";
            }
        }
        return expressionBody;
    }
}
