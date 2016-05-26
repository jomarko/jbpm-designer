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
package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.jbpm.designer.client.wizard.pages.inputs.InputDeletedEvent;
import org.jbpm.designer.client.wizard.pages.widget.ListTaskDetail;
import org.jbpm.designer.client.wizard.util.CompareUtils;
import org.jbpm.designer.client.wizard.util.DefaultValues;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.*;
import org.jbpm.designer.service.DiscoverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.workbench.events.NotificationEvent;


import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
public class ProcessTasksPage implements WizardPage, ProcessTasksPageView.Presenter {

    private static final Logger logger = LoggerFactory.getLogger(ProcessTasksPage.class);

    private static int RESULT_MAX_COUNT = 100;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    @Inject
    private ClientUserSystemManager manager;

    @Inject
    ProcessTasksPageView view;

    private GuidedProcessWizard wizard;

    private WizardPageStatusChangeEvent pageChanged = new WizardPageStatusChangeEvent(this);

    @Inject
    private Event<WizardPageStatusChangeEvent> event;

    @Inject
    private Event<NotificationEvent> notification;

    private DefaultValues defaultValues = new DefaultValues();

    @Inject
    Caller<DiscoverService> discoverService;

    private List<String> discoveredDataTypes = new ArrayList<String>();

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.processTasks();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        boolean allTasksValid = true;
        boolean flowCompletelyTerminated = false;
        int rowsCount = view.getRowsCount();
        List<Task> previousRow = null;
        List<String> taskNames = new ArrayList<String>();
        view.setUniqueNameHelpVisibility(false);
        for(int row = 0; row < rowsCount; row++) {
            List<Task> currentRow = view.getTasks(row);
            for(Task task : currentRow) {
                if(taskNames.contains(task.getName())) {
                    allTasksValid = false;
                    view.setUniqueNameHelpVisibility(true);
                    view.showAsInvalid(task.getId());
                } else if(!isTaskValid(task)) {
                    allTasksValid = false;
                    view.showAsInvalid(task.getId());
                } else {
                    view.showAsValid(task.getId());
                }

                taskNames.add(task.getName());
                if(previousRow != null) {
                    boolean canContinue = false;
                    for(Task taskOfPreviousRow : previousRow) {
                        if(!taskOfPreviousRow.isEndFlow()) {
                            canContinue = true;
                        }
                    }
                    if(!flowCompletelyTerminated && !canContinue) {
                        flowCompletelyTerminated = true;
                    }
                    if(flowCompletelyTerminated) {
                        allTasksValid = false;
                        view.showAsInvalid(task.getId());
                    }
                }
            }
            for(Widget widget : view.getWidgets(row)) {
                if(widget instanceof ListTaskDetail && ((ListTaskDetail) widget).getCondition() != null) {
                    ListTaskDetail detail = (ListTaskDetail)widget;
                    if(!validateCondition(detail.getCondition(), false)) {
                        allTasksValid = false;
                        if(detail.getModel() != null) {
                            view.showAsInvalid(detail.getModel().getId());
                        }
                    }
                }
            }
            previousRow = currentRow;
        }
        callback.callback(allTasksValid);
    }

    @Override
    public void initialise() {
        view.init(this);
        discoverService.call(new RemoteCallback<List<String>>() {
            @Override
            public void callback(List<String> dataTypes) {
                discoveredDataTypes.clear();
                discoveredDataTypes.addAll(dataTypes);
                view.setAvailableDataTypes(dataTypes);
                event.fire(pageChanged);
            }
        }, new DefaultErrorCallback()).getExistingDataTypes();
        sendRequestForExistingUsers(1);
        sendRequestForExistingGroups(1);
        event.fire(pageChanged);
    }

    @Override
    public void prepareView() {
        extractOperations(wizard.getSwaggers());
        view.setTaskPanelVisibility(false);
        view.setConditionPanelVisibility(false);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setWizard(GuidedProcessWizard wizard) {
        this.wizard = wizard;
    }

    @Override
    public Task getDefaultModel(String taskType) {
        Task task = null;

        if(taskType.compareTo(ProcessTasksPageViewImpl.HUMAN_TYPE) == 0) {
            task = defaultValues.getDefaultHumanTask();
        } else {
            task = defaultValues.getDefaultServiceTask();
        }

        firePageChangedEvent();

        return task;
    }

    @Override
    public void firePageChangedEvent() {
        event.fire(pageChanged);
    }

    @Override
    public void splitTasks() {
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets.size() == 2) {
            if(areAllWidgetsMerged(selectedWidgets) && areFromSameRow(selectedWidgets)) {
                ((ListTaskDetail)selectedWidgets.get(0)).setMerged(false);
                ((ListTaskDetail)selectedWidgets.get(1)).setMerged(false);
                ((ListTaskDetail) selectedWidgets.get(0)).setCondition(null);
                ((ListTaskDetail) selectedWidgets.get(1)).setCondition(null);
                view.splitSelectedWidgets();
                view.deselectAll();
                view.setSplitButtonVisibility(false);
                view.setConditionPanelVisibility(false);
                view.setTaskPanelVisibility(false);
                firePageChangedEvent();
            }
        } else {
            view.showSplitInvalidCount();
        }

    }

    @Override
    public void mergeTasks(boolean conditionBased) {
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets.size() == 2) {
            if(canBeMerged(selectedWidgets)) {
                ((ListTaskDetail)selectedWidgets.get(0)).setMerged(true);
                ((ListTaskDetail)selectedWidgets.get(1)).setMerged(true);
                ((ListTaskDetail)selectedWidgets.get(0)).setIsMergedWith(((ListTaskDetail) selectedWidgets.get(1)).getId());
                ((ListTaskDetail)selectedWidgets.get(1)).setIsMergedWith(((ListTaskDetail) selectedWidgets.get(0)).getId());
                if(conditionBased) {
                    ((ListTaskDetail) selectedWidgets.get(0)).setCondition(new Condition());
                    ((ListTaskDetail) selectedWidgets.get(1)).setCondition(new Condition());
                }
                view.mergeSelectedWidgets(conditionBased);
                view.deselectAll();
                view.setMergeButtonsVisibility(false);
                view.setTaskPanelVisibility(false);
                firePageChangedEvent();
            } else {
                view.showAlreadyContainsMerged();
            }
        } else {
            view.showMergeInvalidCount();
        }
    }

    @Override
    public void rowDeleted() {
        view.deselectAll();
        view.setSplitButtonVisibility(false);
        view.setMergeButtonsVisibility(false);
        view.setConditionPanelVisibility(false);
        view.setTaskPanelVisibility(false);
        firePageChangedEvent();
    }

    @Override
    public void taskDetailSelected(ListTaskDetail detail) {
        view.setConditionPanelVisibility(view.getSelectedWidgets().size() == 1 && detail.getCondition() != null);
        view.setTaskPanelVisibility(view.getSelectedWidgets().size() == 1);

        Task model = detail.getModel();
        rebindSelectedWidget(detail, model);
        view.restrictOutputDataTypes();

        List<Widget> selectedWidgets = view.getSelectedWidgets();

        if(selectedWidgets.size() != 2) {
            view.setMergeButtonsVisibility(false);
            view.setSplitButtonVisibility(false);
        } else if(canBeMerged(selectedWidgets)) {
            view.setMergeButtonsVisibility(true);
            view.setSplitButtonVisibility(false);
        } else {
            view.setMergeButtonsVisibility(false);
            if(areFromSameRow(selectedWidgets)) {
                view.setSplitButtonVisibility(true);
            } else {
                view.setSplitButtonVisibility(false);
            }
        }
        firePageChangedEvent();
    }

    private void rebindSelectedWidget(ListTaskDetail detail, Task model) {
        view.unbindAllTaskWidgets();

        if (model instanceof HumanTask) {
            view.showHumanSpecificDetails();
        }
        if (model instanceof ServiceTask) {
            view.showServiceSpecificDetails();
            view.setAcceptableOperations(getAcceptableOperations((ServiceTask) model));
        }

        List<Variable> basicAvailable = getVariablesForTask(model);
        view.setAcceptableVariablesForInputs(basicAvailable);

        detail.setModel(model);
        view.setModelTaskDetailWidgets(model);

        view.setAcceptableVariablesForConditions(basicAvailable);
        if (detail.getCondition() != null) {
            view.rebindConditionWidgetToModel(detail.getCondition());
        }
        detail.rebind();
        view.rebindTaskDetailWidgets();
        view.highlightSelected();
    }


    public List<Variable> getVariablesForTask(Task task) {
        List<Variable> possibleInputs = wizard.getInitialInputs();
        for(Map.Entry<Integer, List<Task>> tasksGroup : getTasks().entrySet()) {
            if(task != null && !tasksGroup.getValue().contains(task)) {
                for(Task previousTask : tasksGroup.getValue()) {
                    Variable taskOutput = null;
                    if (previousTask.getOutputs() != null && previousTask.getOutputs().size() == 1) {
                        taskOutput = previousTask.getOutputs().get(0);
                        if (taskOutput != null && taskOutput.getName() != null &&
                                !taskOutput.getName().isEmpty() && !possibleInputs.contains(taskOutput)) {
                            possibleInputs.add(taskOutput);
                        }
                    }
                }
            } else {
                return possibleInputs;
            }
        }
        return possibleInputs;
    }

    @Override
    public Map<SwaggerParameter, List<Variable>> getAcceptableVariablesForParameter(ServiceTask model, Operation operation) {
        Map<SwaggerParameter, List<Variable>> acceptableVariablesForParameter = new HashMap<SwaggerParameter, List<Variable>>();
        if(model != null) {
            List<Variable> variables = getVariablesForTask(model);
            List<Variable> subTypes = new ArrayList<Variable>(variables);
            for (Variable variable : variables) {
                Map<String, SwaggerDefinition> definitions = wizard.getDefinitions();
                for (String definitionKey : definitions.keySet()) {
                    if (CompareUtils.areSchemeAndDataTypeSame(definitionKey, variable.getDataType())) {
                        for (Map.Entry<String, SwaggerProperty> property : definitions.get(definitionKey).getProperties().entrySet()) {
                            if (property.getValue().getType() != null) {
                                Variable subVariable = new Variable();
                                subVariable.setName(variable.getName() + "." + property.getKey());
                                subVariable.setDataType(typeToUpper(property.getValue().getType()));
                                subTypes.add(subVariable);
                            }
                            if (property.getValue().get$ref() != null) {
                                for (String dataType : discoveredDataTypes) {
                                    if (CompareUtils.areSchemeAndDataTypeSame(property.getValue().get$ref(), dataType)) {
                                        Variable subVariable = new Variable();
                                        subVariable.setName(variable.getName() + "." + property.getKey());
                                        subVariable.setDataType(dataType);
                                        subTypes.add(subVariable);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (operation != null && operation.getParameterMappings() != null) {
                for (ParameterMapping mapping : operation.getParameterMappings()) {
                    if (mapping.getParameter() != null) {
                        SwaggerParameter parameter = mapping.getParameter();
                        List<Variable> acceptable = new ArrayList<Variable>(subTypes);
                        if (parameter.getSchema() != null) {
                            for (Variable variable : subTypes) {
                                String variableDataType = variable.getDataType();
                                if (!CompareUtils.areSchemeAndDataTypeSame(parameter.getSchema(), variableDataType)) {
                                    acceptable.remove(variable);
                                }
                            }
                        } else if (parameter.getType() != null) {
                            for (Variable variable : subTypes) {
                                if (variable.getDataType().compareToIgnoreCase(parameter.getType()) != 0) {
                                    acceptable.remove(variable);
                                }
                            }
                        }

                        acceptableVariablesForParameter.put(parameter, acceptable);
                        if (acceptable.size() == 0) {
                            notification.fire(new NotificationEvent(
                                    DesignerEditorConstants.INSTANCE.noCompatibleVariableForParameter() + " " + parameter.getName(),
                                    NotificationEvent.NotificationType.ERROR));
                        }
                    }
                }
            }
        }

        return acceptableVariablesForParameter;
    }

    private String typeToUpper(String type) {
        if(type == null || type.isEmpty()) {
            return "";
        }
        if(type.length() == 1) {
            return type.toUpperCase();
        }

        return type.substring(0,1).toUpperCase() + type.substring(1, type.length());
    }

    @Override
    public boolean isTaskValid(Task task) {
        if(task == null) {
            return false;
        }

        if(task.getName() == null || task.getName().isEmpty()) {
            return false;
        }

        if(task instanceof ServiceTask) {
            ServiceTask serviceTask = (ServiceTask) task;
            if(serviceTask.getOperation() == null || !checkRequiredMappings(serviceTask.getOperation().getParameterMappings())) {
                return false;
            }
        }

        if(task instanceof HumanTask) {
            HumanTask humanTask = (HumanTask) task;
            if(humanTask.getResponsibleHuman() == null && humanTask.getResponsibleGroup() == null) {
                return false;
            }
        }

        return true;
    }

    private boolean checkRequiredMappings(List<ParameterMapping> mappings) {
        if(mappings != null) {
            for (ParameterMapping parameterMapping : mappings) {
                if (parameterMapping.isRequired() && parameterMapping.getVariable() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateCondition(Condition condition, boolean showHelps) {
        boolean validVariable = true;
        boolean validConstraint = true;
        boolean validValue = true;
        if(condition == null) {
            return false;
        }

        String dataType = null;
        if(condition.getVariable() == null) {
            validVariable = false;
        } else {
            dataType = condition.getVariable().getDataType();
        }

        if(condition.getConstraint() == null || condition.getConstraint().trim().isEmpty()) {
            validConstraint = false;
        }

        if(dataType == null || dataType.compareTo("Boolean") != 0) {
            if (condition.getConstraintValue() == null || condition.getConstraintValue().trim().isEmpty()) {
                validValue = false;
            }
        }


        if(showHelps) {
            view.setVariableHelpVisibility(!validVariable);
            view.setConstraintHelpVisibility(!validConstraint);
            view.setConstraintValueHelpVisibility(!validValue);
        }

        return validVariable && validConstraint && validValue;
    }

    public Map<Integer, List<Task>> getTasks() {
        Map<Integer, List<Task>> tasks = new HashMap<Integer, List<Task>>();
        int rowsCount = view.getRowsCount();
        for(int row = 0; row < rowsCount; row++) {
            tasks.put(row, view.getTasks(row));
        }
        return tasks;
    }

    public Map<Integer, List<Condition>> getMergedRowsWithConditions() {
        Map<Integer, List<Condition>> rowsWithConditions = new HashMap<Integer, List<Condition>>();
        for (int row = 0; row < view.getRowsCount(); row++) {
            List<Widget> rowWidgets = view.getWidgets(row);
            List<Condition> rowConditions = new ArrayList<Condition>();
            for(Widget widget : rowWidgets) {
                if(widget instanceof ListTaskDetail) {
                    ListTaskDetail detail = (ListTaskDetail) widget;
                    if(detail.isMerged() && detail.getCondition() != null) {
                        rowConditions.add(detail.getCondition());
                    }
                }
            }
            if(rowConditions.size() > 1) {
                rowsWithConditions.put(row, rowConditions);
            }
        }

        return rowsWithConditions;
    }

    public void showHelpForSelectedTask(@Observes WizardPageStatusChangeEvent event) {
        Task task = null;
        Condition condition = null;
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets != null && selectedWidgets.size() > 0) {
            Widget lastWidget = selectedWidgets.get(selectedWidgets.size() - 1);
            if(lastWidget instanceof ListTaskDetail) {
                task = ((ListTaskDetail)lastWidget).getModel();
                condition = ((ListTaskDetail) lastWidget).getCondition();
            }
        }

        if (task != null) {
            if (task.getName() == null || task.getName().trim().isEmpty()) {
                view.setNameHelpVisibility(true);
            } else {
                view.setNameHelpVisibility(false);
            }

            if (task instanceof  HumanTask) {
                HumanTask humanTask = (HumanTask) task;
                if (humanTask.getResponsibleHuman() == null && humanTask.getResponsibleGroup() == null) {
                    view.setParticipantHelpVisibility(true);
                } else {
                    view.setParticipantHelpVisibility(false);
                }
            }

            if (task instanceof ServiceTask) {
                ServiceTask serviceTask = (ServiceTask) task;
                if (serviceTask.getOperation() == null) {
                    view.setOperationHelpVisibility(true);
                } else if(!checkRequiredMappings(serviceTask.getOperation().getParameterMappings())) {
                    view.setOperationHelpVisibility(false);
                    view.setOperationParametersHelpVisibility(true);
                } else {
                    view.setOperationHelpVisibility(false);
                    view.setOperationParametersHelpVisibility(false);
                }
            }
        }
        if(condition != null) {
            validateCondition(condition, true);
        }
    }

    public void removeNonExistingBindings(@Observes InputDeletedEvent event) {
        Variable deletedInput = event.getDeletedInput();
        Map<Integer, List<Task>> tasks = getTasks();
        for(Map.Entry<Integer, List<Task>> row : tasks.entrySet()) {
            for(Task task : row.getValue()) {
                if(task.getInputs() != null && task.getInputs().values() != null && task.getInputs().values().contains(deletedInput)) {
                    Map<String, Variable> newInputs = new HashMap<String, Variable>(task.getInputs());
                    for(Map.Entry<String, Variable> entry : task.getInputs().entrySet()) {
                        if(deletedInput == entry.getValue()) {
                            newInputs.remove(entry.getKey());
                        }
                    }
                    task.setInputs(newInputs);
                }
                if(task instanceof ServiceTask) {
                    Operation operation = ((ServiceTask) task).getOperation();
                    if(operation != null && operation.getParameterMappings() != null) {
                        for(ParameterMapping mapping : operation.getParameterMappings()) {
                            if(mapping.getVariable() == deletedInput) {
                                mapping.setVariable(null);
                            }
                        }
                    }
                }
            }
        }
        firePageChangedEvent();
    }

    private boolean areFromSameRow(List<Widget> widgets) {
        List<Integer> from = new ArrayList<Integer>();
        List<Integer> to = new ArrayList<Integer>();

        for (Widget widget : widgets) {
            if(widget instanceof ListTaskDetail) {
                ListTaskDetail detail = (ListTaskDetail) widget;
                if(detail.isMerged()) {
                    from.add(detail.getId());
                    to.add(detail.getIsMergedWith());
                }
            }
        }

        if(from.size() > 0 && to.size() > 0 && from.containsAll(to) && to.containsAll(from)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canBeMerged(List<Widget> widgets) {
        for(Widget widget : widgets) {
            if (((ListTaskDetail)widget).isMerged()) {
                return false;
            }
        }
        return true;
    }

    private boolean areAllWidgetsMerged(List<Widget> widgets) {
        for(Widget widget : widgets) {
            if (!((ListTaskDetail)widget).isMerged()) {
                return false;
            }
        }
        return true;
    }

    private List<Operation> extractOperations(List<Swagger> swaggers) {
        List<Operation> operations = new ArrayList<Operation>();
        if (swaggers != null) {
            for (Swagger swagger : swaggers) {
                if (swagger!= null && swagger.getPaths() != null && swagger.getPaths().entrySet() != null) {
                    for (Map.Entry<String, SwaggerPath> path : swagger.getPaths().entrySet()) {
                        if (path != null && path.getValue() != null) {
                            if (path.getValue().getGet() != null) {
                                Operation operation = new Operation();
                                operation.setUrl(swagger.getHost() + swagger.getBasePath() + path.getKey());
                                operation.setMethod(METHOD_GET);
                                SwaggerOperation swaggerOperation = path.getValue().getGet();
                                extractOperation(swaggerOperation, operation, operations);
                            }
                            if (path.getValue().getPost() != null) {
                                Operation operation = new Operation();
                                operation.setUrl(swagger.getHost() + swagger.getBasePath() + path.getKey());
                                operation.setMethod(METHOD_POST);
                                SwaggerOperation swaggerOperation = path.getValue().getPost();
                                extractOperation(swaggerOperation, operation, operations);
                            }
                            if (path.getValue().getDelete() != null) {
                                Operation operation = new Operation();
                                operation.setUrl(swagger.getHost() + swagger.getBasePath() + path.getKey());
                                operation.setMethod(METHOD_DELETE);
                                SwaggerOperation swaggerOperation = path.getValue().getDelete();
                                extractOperation(swaggerOperation, operation, operations);
                            }
                        }
                    }
                }
            }
        }
        return operations;
    }

    private void extractOperation(SwaggerOperation swaggerOperation, Operation operation, List<Operation> operations) {
        if(swaggerOperation != null) {
            operation.setOperationId(swaggerOperation.getOperationId());
            operation.setDescription(swaggerOperation.getDescription());
            List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
            for (SwaggerParameter swaggerParameter : swaggerOperation.getParameters()) {
                ParameterMapping parameterMapping = new ParameterMapping();
                parameterMapping.setParameter(swaggerParameter);
                parameterMapping.setRequired(swaggerParameter.isRequired());
                parameterMappings.add(parameterMapping);
            }
            operation.setParameterMappings(parameterMappings);
            if(swaggerOperation.getResponses() != null
                    && swaggerOperation.getResponses().get("200") != null
                    && swaggerOperation.getResponses().get("200").getSchema() != null) {
                operation.setResponseScheme(swaggerOperation.getResponses().get("200").getSchema());
            }
            operations.add(operation);
        }
    }

    private List<Operation> getAcceptableOperations(ServiceTask task) {
        List<Operation> available = extractOperations(wizard.getSwaggers());
        if(task.getOperation() == null) {
            return available;
        } else {
            List<Operation> acceptable = new ArrayList<Operation>(available);
            for(Operation operation : available) {
                if(operation.getOperationId() != null && task.getOperation().getOperationId() != null &&
                        operation.getOperationId().compareTo(task.getOperation().getOperationId()) == 0) {
                    acceptable.remove(operation);
                    acceptable.add(task.getOperation());
                }
            }
            return acceptable;
        }
    }

    private void sendRequestForExistingUsers(final int getResultFromPage) {
        manager.users(new RemoteCallback<AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.identity.User>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.identity.User> response) {
                List<org.jbpm.designer.model.User> availableUsers = new ArrayList<org.jbpm.designer.model.User>();
                for(org.jboss.errai.security.shared.api.identity.User u : response.getResults()) {
                    availableUsers.add(new org.jbpm.designer.model.User(u.getIdentifier()));
                }
                view.addAvailableHumanParticipants(availableUsers);
                if(availableUsers.size() == RESULT_MAX_COUNT) {
                    sendRequestForExistingUsers(getResultFromPage + 1);
                }
            }
        }, new ErrorCallback() {
            @Override
            public boolean error(Object o, Throwable throwable) {
                logger.error("Request for existing users failed: " + throwable.getMessage());
                return true;
            }
        }).search(new SearchRequestImpl("", getResultFromPage, RESULT_MAX_COUNT));
    }

    private void sendRequestForExistingGroups(final int getResultFromPage) {
        manager.groups(new RemoteCallback<AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.Group>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.Group> response) {
                List<Group> availableGroups = new ArrayList<Group>();
                for(org.jboss.errai.security.shared.api.Group g : response.getResults()) {
                    availableGroups.add(new Group(g.getName()));
                }
                view.addAvailableGroupParticipants(availableGroups);
                if(availableGroups.size() == RESULT_MAX_COUNT) {
                    sendRequestForExistingUsers(getResultFromPage + 1);
                }
            }
        }, new ErrorCallback() {
            @Override
            public boolean error(Object o, Throwable throwable) {
                logger.error("Request for existing groups failed: " + throwable.getMessage());
                return true;
            }
        }).search(new SearchRequestImpl("", getResultFromPage, RESULT_MAX_COUNT));
    }
}
