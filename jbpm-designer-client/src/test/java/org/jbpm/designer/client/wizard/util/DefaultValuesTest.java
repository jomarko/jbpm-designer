package org.jbpm.designer.client.wizard.util;


import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Variable;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultValuesTest {

    private DefaultValues defaultValues = new DefaultValues();

    @Test
    public void testDefaultVariable() throws Exception {
        Variable variable = defaultValues.getDefaultVariable();
        assertEquals("String", variable.getDataType());
        assertEquals("", variable.getName());
    }

    @Test
    public void testDefaultServiceTask() throws Exception {
        ServiceTask serviceTask = defaultValues.getDefaultServiceTask();
        assertEquals("", serviceTask.getName());
        assertTrue(serviceTask.getInputs().isEmpty());
        assertTrue(serviceTask.getOutputs().isEmpty());
    }

    @Test
    public void testDefaultHumanTask() throws Exception {
        HumanTask humanTask = defaultValues.getDefaultHumanTask();
        assertEquals("", humanTask.getName());
        assertTrue(humanTask.getInputs().isEmpty());
        assertTrue(humanTask.getOutputs().isEmpty());
    }
}
