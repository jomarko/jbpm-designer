/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.server.service;

import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.*;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.di.DiagramElement;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshaller;
import org.jbpm.designer.model.*;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Dependent
public class WizardModelToXmlConverter {

    protected Process process = null;
    protected BPMNDiagram diagram = null;
    protected Definitions definitions = null;
    private JbpmProfileImpl profile = new JbpmProfileImpl();


    public String convertProcessToXml(BusinessProcess businessProcess) {
        createProcess(businessProcess.getProcessName(), businessProcess.getProcessDocumentation());
        createProcessVariables(businessProcess.getVariables());
        int horizontalOffset = 100;
        FlowElement from = createStartEvent(horizontalOffset, businessProcess.getStartEvent());
        FlowElement to;
        if(businessProcess.getTasks() != null) {
            for (Integer row : businessProcess.getTasks().keySet()) {
                if (businessProcess.getTasks().get(row).size() > 1) {
                    horizontalOffset += 150;
                    boolean createAsExclusive = false;
                    if(businessProcess.getConditions() != null && businessProcess.getConditions().containsKey(row)) {
                        createAsExclusive = true;
                    }
                    FlowElement fromGateway = createGateway(horizontalOffset, createAsExclusive, false);
                    FlowElement toGateway = createGateway(horizontalOffset + 300, createAsExclusive, true);
                    createEdge(from, fromGateway, null);
                    int verticalRelativeOffset = 0;
                    int counter = 0;
                    for (org.jbpm.designer.model.Task task : businessProcess.getTasks().get(row)) {
                        FlowElement middle = createTask(task, horizontalOffset + 150, verticalRelativeOffset);
                        Condition condition = null;
                        if(businessProcess.getConditions() != null && businessProcess.getConditions().containsKey(row)) {
                            condition = businessProcess.getConditions().get(row).get(counter);
                        }
                        SequenceFlow flow = createEdge(fromGateway, middle, condition);
                        if(condition != null && condition.isExecuteIfConstraintSatisfied()) {
                            ((ExclusiveGateway)fromGateway).setDefault(flow);
                        }
                        createEdge(middle, toGateway, null);
                        verticalRelativeOffset += 150;
                    }
                    from = toGateway;
                    horizontalOffset = horizontalOffset + 300;
                } else {
                    for (org.jbpm.designer.model.Task task : businessProcess.getTasks().get(row)) {
                        horizontalOffset += 150;
                        to = createTask(task, horizontalOffset, 0);
                        createEdge(from, to, null);
                        from = to;
                    }
                }
            }
        }
        to = createEndEvent(horizontalOffset + 150);
        createEdge(from, to, null);

        Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
        marshaller.setProfile(profile);

        try {
            return profile.createMarshaller().parseModel(marshaller.marshall(definitions, ""), "");
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert your model to xml", e);
        }
    }

    private String createProcess(String processName, String documentationText) {

        Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
        marshaller.setProfile(profile);

        String processId = UUID.randomUUID().toString();
        process = Bpmn2Factory.eINSTANCE.createProcess();
        process.setId(processId);
        process.setName(processName);
        Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
        documentation.setText(documentationText);
        process.getDocumentation().add(documentation);

        diagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
        BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
        plane.setBpmnElement(process);
        diagram.setPlane(plane);

        definitions = Bpmn2Factory.eINSTANCE.createDefinitions();
        definitions.getRootElements().add(process);
        definitions.getDiagrams().add(diagram);

        return processId;
    }

    private FlowElement createTask(org.jbpm.designer.model.Task task, int horizontalOffset, int verticalRelativeOffset) {
        if (process == null) {
            throw new RuntimeException("Create process at first");
        }

        String taskId = UUID.randomUUID().toString();
        org.eclipse.bpmn2.Task bpmnTask = null;
        if (org.jbpm.designer.model.Task.HUMAN_TYPE.compareTo(task.getTaskType()) == 0) {
            bpmnTask = Bpmn2Factory.eINSTANCE.createUserTask();
            bpmnTask.setId(taskId);
            bpmnTask.setName(task.getName());
            if(task.getResponsibleHuman() != null
                    && task.getResponsibleHuman().getName() != null
                    && !task.getResponsibleHuman().getName().isEmpty()) {
                PotentialOwner potentialOwner = Bpmn2Factory.eINSTANCE.createPotentialOwner();
                FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                expression.setBody(task.getResponsibleHuman().getName());
                ResourceAssignmentExpression resourceAssignmentExpression = Bpmn2Factory.eINSTANCE.createResourceAssignmentExpression();
                resourceAssignmentExpression.setExpression(expression);
                potentialOwner.setResourceAssignmentExpression(resourceAssignmentExpression);
                bpmnTask.getResources().add(potentialOwner);
            }
            if(task.getResponsibleGroup() != null
                    && task.getResponsibleGroup().getName() != null
                    && !task.getResponsibleGroup().getName().isEmpty()) {
                Variable groupId = new Variable();
                groupId.setDataType("String");
                groupId.setName("GroupId");
                if(task.getInputs() == null) {
                    task.setInputs(new ArrayList<Variable>());
                }
                task.getInputs().add(groupId);
            }
        }

        if (org.jbpm.designer.model.Task.SERVICE_TYPE.compareTo(task.getTaskType()) == 0) {
            bpmnTask = Bpmn2Factory.eINSTANCE.createServiceTask();
            bpmnTask.setId(taskId);
            bpmnTask.setName(task.getName());
        }

        createInputOutputAssociations(bpmnTask, task);

        BPMNShape shape = getShape(100, 80, horizontalOffset, 100 + verticalRelativeOffset);
        shape.setBpmnElement(bpmnTask);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(bpmnTask);

        return bpmnTask;
    }

    private void createInputOutputAssociations(org.eclipse.bpmn2.Task bpmnTask, org.jbpm.designer.model.Task wizardTask) {
        InputOutputSpecification specification = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
        InputSet inputSet = Bpmn2Factory.eINSTANCE.createInputSet();
        OutputSet outputSet = Bpmn2Factory.eINSTANCE.createOutputSet();

        if(wizardTask.getInputs() != null) {
            for (Variable variable : wizardTask.getInputs()) {
                DataInput dataInput = Bpmn2Factory.eINSTANCE.createDataInput();
                setItemSubjectRef(dataInput, variable);

                DataInputAssociation inputAssociation = Bpmn2Factory.eINSTANCE.createDataInputAssociation();

                if("GroupId".compareTo(variable.getName()) == 0) {
                    dataInput.setName(variable.getName());
                    Assignment assignment = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    fromExpression.setBody(wizardTask.getResponsibleGroup().getName());
                    assignment.setFrom(fromExpression);
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dataInput.getId());
                    assignment.setTo(toExpression);
                    inputAssociation.getAssignment().add(assignment);
                } else {
                    dataInput.setName(variable.getName() + "_inner");
                    for (Property property : process.getProperties()) {
                        if (property.getName().compareTo(variable.getName()) == 0) {
                            inputAssociation.getSourceRef().add(property);
                        }
                    }
                }

                inputAssociation.setTargetRef(dataInput);
                specification.getDataInputs().add(dataInput);
                inputSet.getDataInputRefs().add(dataInput);
                bpmnTask.getDataInputAssociations().add(inputAssociation);
            }

            specification.getInputSets().add(inputSet);
        }

        Variable taskOutput = wizardTask.getOutput();
        if(taskOutput != null && taskOutput.getName() != null && !taskOutput.getName().isEmpty()) {
            DataOutput dataOutput = Bpmn2Factory.eINSTANCE.createDataOutput();
            setItemSubjectRef(dataOutput, taskOutput);
            dataOutput.setName(taskOutput.getName() + "_inner");

            DataOutputAssociation outputAssociation = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
            for (Property property : process.getProperties()) {
                if (property.getName().compareTo(taskOutput.getName()) == 0) {
                    outputAssociation.setTargetRef(property);
                }
            }

            outputAssociation.getSourceRef().add(dataOutput);

            specification.getDataOutputs().add(dataOutput);
            outputSet.getDataOutputRefs().add(dataOutput);
            bpmnTask.getDataOutputAssociations().add(outputAssociation);

            specification.getOutputSets().add(outputSet);
        }

        bpmnTask.setIoSpecification(specification);
    }

    private void setItemSubjectRef(ItemAwareElement element, Variable variable) {
        String id = UUID.randomUUID().toString() + "_" + variable.getName();

        ItemDefinition itemDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        itemDefinition.setStructureRef(variable.getDataType());
        itemDefinition.setId(id + "_item");

        element.setId(id);
        element.setItemSubjectRef(itemDefinition);
    }

    private void createProcessVariables(List<Variable> variables) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        ItemDefinition stringDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        stringDefinition.setId(UUID.randomUUID().toString());
        stringDefinition.setStructureRef("String");

        ItemDefinition booleanDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        booleanDefinition.setId(UUID.randomUUID().toString());
        booleanDefinition.setStructureRef("Boolean");

        ItemDefinition floatDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        floatDefinition.setId(UUID.randomUUID().toString());
        floatDefinition.setStructureRef("Float");

        if(variables != null) {
            for (Variable variable : variables) {
                Property property = Bpmn2Factory.eINSTANCE.createProperty();
                property.setId(variable.getName());
                property.setName(variable.getName());
                if (variable.getDataType().compareTo("String") == 0) {
                    property.setItemSubjectRef(stringDefinition);
                }
                if (variable.getDataType().compareTo("Boolean") == 0) {
                    property.setItemSubjectRef(booleanDefinition);
                }
                if (variable.getDataType().compareTo("Float") == 0) {
                    property.setItemSubjectRef(floatDefinition);
                }
                process.getProperties().add(property);
            }
        }
    }

    private FlowElement createGateway(int horizontalOffset, boolean exclusive, boolean converging) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();
        Gateway gateway = null;
        if (exclusive) {
            gateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
        } else {
            gateway = Bpmn2Factory.eINSTANCE.createParallelGateway();
        }
        if (converging) {
            gateway.setGatewayDirection(GatewayDirection.CONVERGING);
        } else {
            gateway.setGatewayDirection(GatewayDirection.DIVERGING);
        }
        gateway.setId(id);

        BPMNShape shape = getShape(40, 40, horizontalOffset, 100);
        shape.setBpmnElement(gateway);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(gateway);
        return gateway;
    }

    private SequenceFlow createEdge(FlowElement from, FlowElement to, Condition condition) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        BPMNShape fromShape = getShapeByElement(from);
        BPMNShape toShape = getShapeByElement(to);

        String id = UUID.randomUUID().toString();
        SequenceFlow flow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
        flow.setSourceRef((FlowNode) from);
        flow.setTargetRef((FlowNode) to);
        flow.setId(id);

        if(condition != null) {
            Constraint constraint = condition.getConstraint();
            if (constraint != null && constraint.getConstraint() != null && constraint.getVariable() != null && constraint.getConstraintValue() != null) {
                FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                expression.setId(UUID.randomUUID().toString());
                String expressionBody = "return KieFunctions.equalsTo(";
                expressionBody += constraint.getVariable().getName();
                expressionBody += ",\"";
                expressionBody += constraint.getConstraintValue();
                expressionBody += "\");";
                expression.setBody(expressionBody);
                flow.setConditionExpression(expression);
            }
        }

        BPMNEdge edge = BpmnDiFactory.eINSTANCE.createBPMNEdge();
        edge.setSourceElement(fromShape);
        edge.setTargetElement(toShape);
        edge.setBpmnElement(flow);

        diagram.getPlane().getPlaneElement().add(edge);
        process.getFlowElements().add(flow);
        return flow;
    }

    private BPMNShape getShapeByElement(FlowElement element) {
        for(DiagramElement shape : diagram.getPlane().getPlaneElement()) {

            if((shape instanceof BPMNShape) && ((BPMNShape)shape).getBpmnElement() == element) {
                return (BPMNShape) shape;
            }
        }

        return null;
    }

    private FlowElement createStartEvent(int horizontalOffset, StandardEvent startEvent) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();

        StartEvent start = Bpmn2Factory.eINSTANCE.createStartEvent();
        start.setId(id);
        if(startEvent instanceof SignalEvent) {
            Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
            signal.setName(((SignalEvent)startEvent).getSignalName());
            signal.setId(UUID.randomUUID().toString());
            definitions.getRootElements().add(signal);
            SignalEventDefinition signalED = Bpmn2Factory.eINSTANCE.createSignalEventDefinition();
            signalED.setSignalRef(signal.getId());
            signalED.setId(UUID.randomUUID().toString());
            start.getEventDefinitions().add(signalED);
        }
        if(startEvent instanceof TimerEvent) {
            TimerEventDefinition timer = Bpmn2Factory.eINSTANCE.createTimerEventDefinition();
            String type = ((TimerEvent)startEvent).getTimerType();
            FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            expression.setBody(((TimerEvent)startEvent).getTimerExpression());
            expression.setId(UUID.randomUUID().toString());
            if(TimerEvent.DURATION.compareTo(type) == 0) {
                timer.setTimeDuration(expression);
            }
            if(TimerEvent.CYCLE.compareTo(type) == 0) {
                timer.setTimeCycle(expression);
            }
            if(TimerEvent.DATE.compareTo(type) == 0) {
                timer.setTimeDate(expression);
            }
            timer.setId(UUID.randomUUID().toString());
            start.getEventDefinitions().add(timer);
        }

        BPMNShape shape = getShape(28, 28, horizontalOffset, 100);
        shape.setBpmnElement(start);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(start);
        return start;
    }

    private FlowElement createEndEvent(int horizontalOffset) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();
        EndEvent end = Bpmn2Factory.eINSTANCE.createEndEvent();
        end.setId(id);

        BPMNShape shape = getShape(28, 28, horizontalOffset, 100);
        shape.setBpmnElement(end);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(end);
        return end;
    }

    private BPMNShape getShape(int width, int height, int x, int y) {
        BPMNShape shape = BpmnDiFactory.eINSTANCE.createBPMNShape();
        Bounds bounds = DcFactory.eINSTANCE.createBounds();
        bounds.setWidth(width);
        bounds.setHeight(height);
        bounds.setX(x);
        bounds.setY(y);

        shape.setBounds(bounds);
        return shape;
    }

}
