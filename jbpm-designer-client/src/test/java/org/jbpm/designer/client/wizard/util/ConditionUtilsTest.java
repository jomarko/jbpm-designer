package org.jbpm.designer.client.wizard.util;


import org.jbpm.designer.model.Condition;
import org.jbpm.designer.model.Variable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ConditionUtilsTest {

    private Condition condition;

    private Variable variable;

    @Before
    public void setUp() throws Exception {
        condition = new Condition();

        variable = new Variable();
    }

    @Test
    public void testReverseTrue() throws Exception {
        condition.setConstraint(Condition.IS_TRUE);
        assertEquals(Condition.IS_FALSE,
                     ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseFalse() throws Exception {
        condition.setConstraint(Condition.IS_FALSE);
        assertEquals(Condition.IS_TRUE,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseContains() throws Exception {
        condition.setConstraint(Condition.CONTAINS);
        assertEquals(Condition.NOT_CONTAINS,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseNotContains() throws Exception {
        condition.setConstraint(Condition.NOT_CONTAINS);
        assertEquals(Condition.CONTAINS,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseLessThan() throws Exception {
        condition.setConstraint(Condition.LESS_THAN);
        assertEquals(Condition.EQUAL_OR_GREATER_THAN,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseEqualGreater() throws Exception {
        condition.setConstraint(Condition.EQUAL_OR_GREATER_THAN);
        assertEquals(Condition.LESS_THAN,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseGreaterThan() throws Exception {
        condition.setConstraint(Condition.GREATER_THAN);
        assertEquals(Condition.EQUAL_OR_LESS_THAN,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseEqualLess() throws Exception {
        condition.setConstraint(Condition.EQUAL_OR_LESS_THAN);
        assertEquals(Condition.GREATER_THAN,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseStart() throws Exception {
        condition.setConstraint(Condition.STARTS_WITH);
        assertEquals(Condition.NOT_STARTS_WITH,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseNotStart() throws Exception {
        condition.setConstraint(Condition.NOT_STARTS_WITH);
        assertEquals(Condition.STARTS_WITH,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseNotEqual() throws Exception {
        condition.setConstraint(Condition.NOT_EQUALS_TO);
        assertEquals(Condition.EQUALS_TO,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testReverseEqual() throws Exception {
        condition.setConstraint(Condition.EQUALS_TO);
        assertEquals(Condition.NOT_EQUALS_TO,
                ConditionUtils.constructReverseCondition(condition).getConstraint());
    }

    @Test
    public void testGetConditionsInteger() throws Exception {
        variable.setDataType("Integer");
        List<String> constraints = ConditionUtils.getConstraints(variable);
        verifyNumbers(constraints);
    }

    @Test
    public void testGetConditionsFloat() throws Exception {
        variable.setDataType("Float");
        List<String> constraints = ConditionUtils.getConstraints(variable);
        verifyNumbers(constraints);
    }

    @Test
    public void testGetConditionsDouble() throws Exception {
        variable.setDataType("Double");
        List<String> constraints = ConditionUtils.getConstraints(variable);
        verifyNumbers(constraints);
    }

    @Test
    public void testGetConditionsString() throws Exception {
        variable.setDataType("String");
        List<String> constraints = ConditionUtils.getConstraints(variable);
        assertEquals(6, constraints.size());
        assertTrue(constraints.contains(Condition.STARTS_WITH));
        assertTrue(constraints.contains(Condition.NOT_STARTS_WITH));
        assertTrue(constraints.contains(Condition.CONTAINS));
        assertTrue(constraints.contains(Condition.NOT_CONTAINS));
        assertTrue(constraints.contains(Condition.EQUALS_TO));
        assertTrue(constraints.contains(Condition.NOT_EQUALS_TO));
    }

    @Test
    public void testGetConditionsBoolean() throws Exception {
        variable.setDataType("Boolean");
        List<String> constraints = ConditionUtils.getConstraints(variable);
        assertEquals(2, constraints.size());
        assertTrue(constraints.contains(Condition.IS_TRUE));
        assertTrue(constraints.contains(Condition.IS_FALSE));
    }

    private void verifyNumbers(List<String> constraints) {
        assertEquals(6, constraints.size());
        assertTrue(constraints.contains(Condition.LESS_THAN));
        assertTrue(constraints.contains(Condition.EQUAL_OR_LESS_THAN));
        assertTrue(constraints.contains(Condition.GREATER_THAN));
        assertTrue(constraints.contains(Condition.EQUAL_OR_GREATER_THAN));
        assertTrue(constraints.contains(Condition.EQUALS_TO));
        assertTrue(constraints.contains(Condition.NOT_EQUALS_TO));
    }
}
