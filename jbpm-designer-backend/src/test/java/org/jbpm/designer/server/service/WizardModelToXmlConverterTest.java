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
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.operation.*;
import org.jbpm.designer.model.operation.Operation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WizardModelToXmlConverterTest {

    private WizardModelToXmlConverter converter;
    private BusinessProcess process;
    private HumanTask humanTask;
    private Variable stringVariable;
    private List<Variable> variables;
    private Map<Integer, List<Task>> taskGroups;

    @Before
    public void init() {
        converter = new WizardModelToXmlConverter();

        process = new BusinessProcess();
        process.setProcessName("someNameOfProcess");
        process.setStartEvent(new StandardEvent());

        humanTask = new HumanTask();
        humanTask.setName("humanTaskName");
        humanTask.setResponsibleHuman(new User("user_nick"));
        humanTask.setResponsibleGroup(new User("group_nick"));
        humanTask.setInputs(new HashMap<String, Variable>());
        humanTask.setOutputs(new ArrayList<Variable>());

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
    public void testTerminateOne() {
        humanTask.setEndFlow(true);

        HumanTask humanTask2 = new HumanTask();
        humanTask2.setName("b");
        humanTask2.setResponsibleHuman(new User("user_nick"));
        humanTask2.setResponsibleGroup(new User("group_nick"));
        humanTask2.setInputs(new HashMap<String, Variable>());
        humanTask2.setOutputs(new ArrayList<Variable>());

        taskGroups.get(0).add(humanTask2);
        process.setTasks(taskGroups);

        converter.convertProcessToXml(process);
        assertEquals(0, converter.process.getProperties().size());
        assertEquals(11, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnGateways(converter.process.getFlowElements(), false).size());
        assertEquals(2, extractEndEvents(converter.process.getFlowElements()).size());
    }

    @Test
    public void testTerminateBoth() {
        humanTask.setEndFlow(true);
        taskGroups.get(0).add(humanTask);
        process.setTasks(taskGroups);

        converter.convertProcessToXml(process);
        assertEquals(0, converter.process.getProperties().size());
        assertEquals(11, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnGateways(converter.process.getFlowElements(), false).size());
        assertEquals(2, extractEndEvents(converter.process.getFlowElements()).size());
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
        List<org.eclipse.bpmn2.Task> tasks = extractBpmnTasks(converter.process.getFlowElements());
        assertEquals(1, tasks.size());
        assertEquals(1, tasks.get(0).getResources().size());
        assertEquals("user_nick",
                ((FormalExpression)tasks.get(0).getResources().get(0).getResourceAssignmentExpression().getExpression()).getBody());
        assertEquals(2, tasks.get(0).getDataInputAssociations().size());
        assertEquals("humanTaskName", ((FormalExpression)tasks.get(0).getDataInputAssociations().get(0).getAssignment().get(0).getFrom()).getBody());
        assertEquals("group_nick", ((FormalExpression)tasks.get(0).getDataInputAssociations().get(1).getAssignment().get(0).getFrom()).getBody());
    }

    @Test
    public void testProcessOneTaskInputs() {
        humanTask.getInputs().put(stringVariable.getName(), stringVariable);
        process.setInitialVariables(variables);
        process.setTasks(taskGroups);

        converter.convertProcessToXml(process);

        assertEquals(1, converter.process.getProperties().size());
        assertEquals(5, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnTasks(converter.process.getFlowElements()).size());

        org.eclipse.bpmn2.Task bpmnTask = extractBpmnTasks(converter.process.getFlowElements()).get(0);
        assertEquals(3, bpmnTask.getIoSpecification().getDataInputs().size());
        assertEquals(3, bpmnTask.getDataInputAssociations().size());
    }

    @Test
    public void testTaskOutput() {
        List<Variable> outputs = humanTask.getOutputs();
        outputs.add(stringVariable);
        humanTask.setOutputs(outputs);
        process.setAdditionalVariables(variables);
        process.setTasks(taskGroups);

        converter.convertProcessToXml(process);

        assertEquals(1, converter.process.getProperties().size());
        assertEquals(5, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnTasks(converter.process.getFlowElements()).size());

        org.eclipse.bpmn2.Task bpmnTask = extractBpmnTasks(converter.process.getFlowElements()).get(0);
        assertEquals(1, bpmnTask.getIoSpecification().getDataOutputs().size());
        assertEquals(1, bpmnTask.getDataOutputAssociations().size());
    }

    @Test
    public void testParallelGateway() {
        taskGroups.get(0).add(humanTask);
        process.setTasks(taskGroups);

        converter.convertProcessToXml(process);

        assertEquals(0, converter.process.getProperties().size());
        assertEquals(12, converter.process.getFlowElements().size());
        assertEquals(2, extractBpmnGateways(converter.process.getFlowElements(), false).size());
    }

    @Test
    public void testExclusiveGateway() {
        taskGroups.get(0).add(humanTask);
        Map<Integer, List<Condition>> conditionGroups = new HashMap<Integer, List<Condition>>();
        List<Condition> conditions = new ArrayList<Condition>();
        Condition positive = new Condition();
        positive.setVariable(stringVariable);
        positive.setConstraint(Condition.EQUALS_TO);
        positive.setConstraintValue("abc");

        Condition negative = new Condition();
        negative.setVariable(stringVariable);
        negative.setConstraint(Condition.EQUALS_TO);
        negative.setConstraintValue("abc");

        conditions.add(positive);
        conditions.add(negative);
        conditionGroups.put(0, conditions);
        process.setConditions(conditionGroups);
        process.setTasks(taskGroups);
        process.setInitialVariables(variables);

        converter.convertProcessToXml(process);

        assertEquals(1, converter.process.getProperties().size());
        assertEquals(12, converter.process.getFlowElements().size());
        assertEquals(2, extractBpmnGateways(converter.process.getFlowElements(), true).size());
    }

    @Test
    public void testRestTask() {
        SwaggerParameter pathParameter = new SwaggerParameter();
        pathParameter.setName("xyz");
        pathParameter.setIn("path");

        SwaggerParameter queryParameter = new SwaggerParameter();
        queryParameter.setName("zyx");
        queryParameter.setIn("query");

        SwaggerParameter secondQueryParameter = new SwaggerParameter();
        secondQueryParameter.setName("xxx");
        secondQueryParameter.setIn("body");

        List<ParameterMapping> mappings = new ArrayList<ParameterMapping>();
        ParameterMapping mapping = new ParameterMapping();
        mapping.setParameter(pathParameter);
        mapping.setVariable(stringVariable);
        mappings.add(mapping);

        ParameterMapping mappingQuery = new ParameterMapping();
        mappingQuery.setParameter(queryParameter);
        mappingQuery.setVariable(stringVariable);
        mappings.add(mappingQuery);

        ParameterMapping mappingQuerySecond = new ParameterMapping();
        mappingQuerySecond.setParameter(secondQueryParameter);
        mappingQuerySecond.setVariable(stringVariable);
        mappings.add(mappingQuerySecond);

        Operation operation = new Operation();
        operation.setMethod("GET");
        operation.setUrl("/{xyz}/{xyz}/{xyz}");
        operation.setParameterMappings(mappings);

        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setName("abc");
        serviceTask.setOperation(operation);

        taskGroups.get(0).clear();
        taskGroups.get(0).add(serviceTask);

        process.setTasks(taskGroups);
        converter.convertProcessToXml(process);

        assertEquals(5, converter.process.getFlowElements().size());
        assertEquals(1, extractBpmnTasks(converter.process.getFlowElements()).size());

        org.eclipse.bpmn2.Task bpmnTask = extractBpmnTasks(converter.process.getFlowElements()).get(0);
        assertEquals(4, bpmnTask.getIoSpecification().getDataInputs().size());
        assertEquals(4, bpmnTask.getDataInputAssociations().size());
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

    private List<Gateway> extractBpmnGateways(List<FlowElement> flowElements, boolean exclusive) {
        List<Gateway> tasks = new ArrayList<Gateway>();
        for(FlowElement element : flowElements) {
            if(exclusive) {
                if (element instanceof ExclusiveGateway) {
                    tasks.add((Gateway) element);
                }
            } else {
                if (element instanceof ParallelGateway) {
                    tasks.add((Gateway) element);
                }
            }
        }

        return tasks;
    }

    private List<EndEvent> extractEndEvents(List<FlowElement> flowElements) {
        List<EndEvent> events = new ArrayList<EndEvent>();
        for(FlowElement element : flowElements) {

            if (element instanceof EndEvent) {
                events.add((EndEvent) element);
            }

        }

        return events;
    }
}
