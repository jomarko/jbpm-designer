package org.jbpm.designer.server.service.util;

import org.jbpm.designer.model.Condition;
import org.jbpm.designer.model.Variable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KieFunctionsGeneratorTest {

    private Condition condition;

    private Variable variable;

    @Before
    public void setUp() throws Exception {
        condition = new Condition();
        variable = new Variable();
        variable.setName("varName");
        condition.setVariable(variable);
        condition.setConstraintValue("xyz");
    }

    @Test
    public void testEqual() throws Exception {
        condition.setConstraint(Condition.EQUALS_TO);
        assertEquals("return KieFunctions.equalsTo(varName,\"xyz\");",
                    KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testNotEqual() throws Exception {
        condition.setConstraint(Condition.NOT_EQUALS_TO);
        assertEquals("return !KieFunctions.equalsTo(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testContains() throws Exception {
        condition.setConstraint(Condition.CONTAINS);
        assertEquals("return KieFunctions.contains(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testNotContains() throws Exception {
        condition.setConstraint(Condition.NOT_CONTAINS);
        assertEquals("return !KieFunctions.contains(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testStart() throws Exception {
        condition.setConstraint(Condition.STARTS_WITH);
        assertEquals("return KieFunctions.startsWith(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testNotStart() throws Exception {
        condition.setConstraint(Condition.NOT_STARTS_WITH);
        assertEquals("return !KieFunctions.startsWith(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testTrue() throws Exception {
        condition.setConstraint(Condition.IS_TRUE);
        assertEquals("return KieFunctions.isTrue(varName);",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testFalse() throws Exception {
        condition.setConstraint(Condition.IS_FALSE);
        assertEquals("return KieFunctions.isFalse(varName);",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testGreater() throws Exception {
        condition.setConstraint(Condition.GREATER_THAN);
        assertEquals("return KieFunctions.greaterThan(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testLess() throws Exception {
        condition.setConstraint(Condition.LESS_THAN);
        assertEquals("return KieFunctions.lessThan(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testGreaterEqual() throws Exception {
        condition.setConstraint(Condition.EQUAL_OR_GREATER_THAN);
        assertEquals("return KieFunctions.greaterOrEqualThan(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

    @Test
    public void testLessEqual() throws Exception {
        condition.setConstraint(Condition.EQUAL_OR_LESS_THAN);
        assertEquals("return KieFunctions.lessOrEqualThan(varName,\"xyz\");",
                KieFunctionsGenerator.getKieFunctionsExpression(condition));
    }

}
