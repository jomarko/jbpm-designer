/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.server.service;

import org.eclipse.bpmn2.*;

import org.jbpm.designer.model.*;
import org.jbpm.designer.model.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WizardModelToXmlConverterTest {

    private WizardModelToXmlConverter converter;
    private BusinessProcess process;
    private Task humanTask;
    private Variable stringVariable;
    private List<Variable> variables;
    private Map<Integer, List<Task>> taskGroups;

    @Before
    public void init() {
        converter = new WizardModelToXmlConverter();

        process = new BusinessProcess();
        process.setStartEvent(new StandardEvent());

        humanTask = new Task();
        humanTask.setName("a");
        humanTask.setResponsibleHuman(new User("user_nick"));
        humanTask.setTaskType(org.jbpm.designer.model.Task.HUMAN_TYPE);
        humanTask.setInputs(new ArrayList<Variable>());

        taskGroups = new HashMap<Integer, List<Task>>();
        List<Task> group = new ArrayList<Task>();
        group.add(humanTask);
        taskGroups.put(0, group);

        stringVariable = new Variable();
        stringVariable.setDataType("String");
        stringVariable.setName("abc");

        variables = new ArrayList<Variable>();
        variables.add(stringVariable);
    }

    @Test
    public void testProcessNoTasks() {
        converter.convertProcessToXml(process);

        assertEquals(0, converter.process.getProperties().size());
        assertEquals(3, converter.process.getFlowElements().size());
        for(FlowElement element : converter.process.getFlowElements()) {
            if(element instanceof Task) {
                fail("There can't be task in process ");
            }
        }
    }

    @Test
    public void testProcessOneTask() {
        process.setTasks(taskGroups);
        converter.convertProcessToXml(process);

        assertEquals(0, converter.process.getProperties().size());
        assertEquals(5, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnTasks(converter.process.getFlowElements()).size());
    }

    @Test
    public void testProcessOneTaskInputs() {
        humanTask.getInputs().add(stringVariable);
        process.setVariables(variables);
        process.setTasks(taskGroups);

        converter.convertProcessToXml(process);

        assertEquals(1, converter.process.getProperties().size());
        assertEquals(5, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnTasks(converter.process.getFlowElements()).size());

        org.eclipse.bpmn2.Task bpmnTask = extractBpmnTasks(converter.process.getFlowElements()).get(0);
        assertEquals(1, bpmnTask.getIoSpecification().getDataInputs().size());
        assertEquals(1, bpmnTask.getDataInputAssociations().size());
    }

    @Test
    public void testTaskOutput() {
        humanTask.setOutput(stringVariable);
        process.setVariables(variables);
        process.setTasks(taskGroups);

        String xml = converter.convertProcessToXml(process);

        assertEquals(1, converter.process.getProperties().size());
        assertEquals(5, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnTasks(converter.process.getFlowElements()).size());

        org.eclipse.bpmn2.Task bpmnTask = extractBpmnTasks(converter.process.getFlowElements()).get(0);
        assertEquals(1, bpmnTask.getIoSpecification().getDataOutputs().size());
        assertEquals(1, bpmnTask.getDataOutputAssociations().size());
    }

    private List<org.eclipse.bpmn2.Task> extractBpmnTasks(List<FlowElement> flowElements) {
        List<org.eclipse.bpmn2.Task> tasks = new ArrayList<org.eclipse.bpmn2.Task>();
        for(FlowElement element : flowElements) {
            if(element instanceof org.eclipse.bpmn2.Task) {
                tasks.add((org.eclipse.bpmn2.Task) element);
            }
        }

        return tasks;
    }
}
