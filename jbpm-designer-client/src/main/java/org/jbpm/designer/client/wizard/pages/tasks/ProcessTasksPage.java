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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.jbpm.designer.client.wizard.pages.widget.ListTaskDetail;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.*;
import org.jbpm.designer.service.SwaggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Dependent
public class ProcessTasksPage implements WizardPage, ProcessTasksPageView.Presenter {

    private static final Logger logger = LoggerFactory.getLogger(ProcessTasksPage.class);
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private static int RESULT_MAX_COUNT = 100;

    @Inject
    ProcessTasksPageView view;

    @Inject
    private ClientUserSystemManager manager;

    private GuidedProcessWizard wizard;

    private WizardPageStatusChangeEvent pageChanged = new WizardPageStatusChangeEvent(this);

    @Inject
    private Event<WizardPageStatusChangeEvent> event;

    @Inject
    private Caller<SwaggerService> swaggerDefinitionService;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.processTasks();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        boolean allTasksValid = true;
        int rowsCount = view.getRowsCount();
        for(int row = 0; row < rowsCount; row++) {
            for(Task task : view.getTasks(row)) {
                if(!isTaskValid(task)) {
                    allTasksValid = false;
                    view.showAsInvalid(task.getId());
                } else {
                    view.showAsValid(task.getId());
                }
            }
        }
        callback.callback(allTasksValid);
    }

    @Override
    public void initialise() {
        view.init(this);
        sendRequestForExistingOperations();
    }

    @Override
    public void prepareView() {
        sendRequestForExistingUsers(1);
        sendRequestForExistingGroups(1);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setWizard(GuidedProcessWizard wizard) {
        this.wizard = wizard;
    }

    @Override
    public Task getDefaultModel() {
        HumanTask task = new HumanTask("");

        Variable output = new Variable();
        output.setName("");
        output.setDataType("String");
        task.setOutput(output);
        task.setInputs(new ArrayList<Variable>());

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
            if(!canBeMerged(selectedWidgets) && areFromSameRow(selectedWidgets)) {
                ((ListTaskDetail)selectedWidgets.get(0)).setMerged(false);
                ((ListTaskDetail)selectedWidgets.get(1)).setMerged(false);
                ((ListTaskDetail) selectedWidgets.get(0)).setCondition(null);
                ((ListTaskDetail) selectedWidgets.get(1)).setCondition(null);
                view.splitSelectedWidgets();
                view.deselectAll();
                view.setSplitButtonVisibility(false);
                view.setConditionPanelVisibility(false);
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
                    Constraint constraint = new Constraint();
                    Condition positive = new Condition();
                    positive.setConstraint(constraint);
                    positive.setExecuteIfConstraintSatisfied(true);
                    Condition negative = new Condition();
                    negative.setConstraint(constraint);
                    negative.setExecuteIfConstraintSatisfied(false);
                    ((ListTaskDetail) selectedWidgets.get(0)).setCondition(positive);
                    ((ListTaskDetail) selectedWidgets.get(1)).setCondition(negative);
                }
                view.mergeSelectedWidgets();
                view.deselectAll();
                view.setMergeButtonsVisibility(false);
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
        firePageChangedEvent();
    }

    @Override
    public void taskDetailSelected(ListTaskDetail detail) {
        view.setConditionPanelVisibility(detail.getCondition() != null);

        Task model = detail.getModel();
        rebindSelectedWidget(detail, model);

        List<Widget> selectedWidgets = view.getSelectedWidgets();

        if(selectedWidgets.size() != 2) {
            view.setMergeButtonsVisibility(false);
            view.setSplitButtonVisibility(false);
        } else if( canBeMerged(selectedWidgets)) {
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

    @Override
    public void selectedWidgetModelChanged(Task model) {
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets.size() == 1 && selectedWidgets.get(0) instanceof ListTaskDetail) {
            if(selectedWidgets.get(0) instanceof ListTaskDetail) {
                ListTaskDetail detail = (ListTaskDetail) selectedWidgets.get(0);
                rebindSelectedWidget(detail, model);
            }
        }
    }

    private void rebindSelectedWidget(ListTaskDetail detail, Task model) {

        view.unbindAllTaskWidgets();

        if (model instanceof HumanTask) {
            view.showHumanSpecificDetails();
        }
        if (model instanceof ServiceTask) {
            view.showServiceSpecificDetails();
        }

        view.setAvailableVarsForSelectedTask(getVariablesForTask(model));
        detail.setModel(model);
        view.setModelTaskDetailWidgets(model);
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
            for(Task previousTask : tasksGroup.getValue()) {
                if(task != null && task != previousTask) {
                    Variable taskOutput = previousTask.getOutput();
                    if (taskOutput != null && taskOutput.getName() != null &&
                        !taskOutput.getName().isEmpty() && !possibleInputs.contains(taskOutput)) {
                        possibleInputs.add(taskOutput);
                    }
                } else {
                    return possibleInputs;
                }
            }
        }
        return possibleInputs;
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
            if(serviceTask.getOperation() == null || !isOperationValid(serviceTask.getOperation())) {
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

    private boolean isOperationValid(Operation operation) {
        boolean valid = true;

        if(operation.getParameterMappings() != null) {
            valid = valid && checkRequiredMappings(operation.getParameterMappings());
        }
        if(operation.getContentParameterMappings() != null) {
            valid = valid && checkRequiredMappings(operation.getContentParameterMappings());
        }
        return valid;
    }

    private boolean checkRequiredMappings(List<ParameterMapping> mappings) {
        for(ParameterMapping parameterMapping : mappings) {
            if(parameterMapping.isRequired() && parameterMapping.getVariable() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isConstraintValid(Constraint constraint) {
        boolean validResult = true;
        if(constraint == null) {
            validResult = validResult && false;;
        }

        if(constraint.getVariable() == null) {
            view.setVariableHelpVisibility(true);
            validResult = validResult && false;
        } else {
            view.setVariableHelpVisibility(false);
        }

        if(constraint.getConstraint() == null || constraint.getConstraint().isEmpty()) {
            view.setConstraintHelpVisibility(true);
            validResult = validResult && false;
        } else {
            view.setConstraintHelpVisibility(false);
        }

        if(constraint.getConstraintValue() == null || constraint.getConstraintValue().isEmpty()) {
            view.setConstraintValueHelpVisibility(true);
            validResult = validResult && false;
        } else {
            view.setConstraintValueHelpVisibility(false);
        }

        return validResult;
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
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets != null && selectedWidgets.size() > 0) {
            Widget lastWidget = selectedWidgets.get(selectedWidgets.size() - 1);
            if(lastWidget instanceof ListTaskDetail) {
                task = ((ListTaskDetail)lastWidget).getModel();
            }
        }

        if (task != null) {
            if (task.getName() == null || task.getName().isEmpty()) {
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
                } else if(!isOperationValid(serviceTask.getOperation())) {
                    view.setOperationHelpVisibility(true);
                } else {
                    view.setOperationHelpVisibility(false);
                }
            }
        }
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

    private void sendRequestForExistingUsers(final int getResultFromPage) {
        manager.users(new RemoteCallback<AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.identity.User>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.identity.User> response) {
                List<User> availableUsers = new ArrayList<User>();
                for(org.jboss.errai.security.shared.api.identity.User u : response.getResults()) {
                    availableUsers.add(new User(u.getIdentifier()));
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

    private void sendRequestForExistingOperations() {
        try {
            swaggerDefinitionService
                    .call(new RemoteCallback<Swagger>() {
                        @Override
                        public void callback(Swagger swagger) {
                            if(swagger != null && swagger.getPaths() != null && swagger.getPaths().entrySet() != null) {
                                for (Map.Entry<String, SwaggerPath> path : swagger.getPaths().entrySet()) {
                                    if(path != null && path.getValue() != null) {
                                        if (path.getValue().getGet() != null) {
                                            Operation operation = new Operation();
                                            operation.setUrl(swagger.getUrlBase() + path.getKey());
                                            operation.setMethod(METHOD_GET);
                                            SwaggerOperation swaggerOperation = path.getValue().getGet();
                                            addOperation(swagger, swaggerOperation, operation);
                                        }
                                        if (path.getValue().getPost() != null) {
                                            Operation operation = new Operation();
                                            operation.setUrl(swagger.getUrlBase() + path.getKey());
                                            operation.setMethod(METHOD_POST);
                                            SwaggerOperation swaggerOperation = path.getValue().getPost();
                                            addOperation(swagger, swaggerOperation, operation);
                                        }
                                        if (path.getValue().getDelete() != null) {
                                            Operation operation = new Operation();
                                            operation.setUrl(swagger.getUrlBase() + path.getKey());
                                            operation.setMethod(METHOD_DELETE);
                                            SwaggerOperation swaggerOperation = path.getValue().getDelete();
                                            addOperation(swagger, swaggerOperation, operation);
                                        }
                                    }
                                }
                            }
                        }
                    }, new DefaultErrorCallback())
                    .getSwagger();
        } catch (IOException e) {
            logger.error("Request for existing operations failed: " + e.getMessage());
        }
    }

    private void addOperation(Swagger swagger, SwaggerOperation swaggerOperation, Operation operation) {
        if(swaggerOperation != null) {
            operation.setOperationId(swaggerOperation.getOperationId());
            operation.setDescription(swaggerOperation.getDescription());
            List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
            List<ParameterMapping> contentParameterMappings = new ArrayList<ParameterMapping>();
            for (SwaggerParameter swaggerParameter : swaggerOperation.getParameters()) {
                if(swaggerParameter.getIn().compareTo("body") != 0) {
                    ParameterMapping parameterMapping = new ParameterMapping();
                    parameterMapping.setParameter(swaggerParameter);
                    parameterMapping.setRequired(swaggerParameter.isRequired());
                    parameterMappings.add(parameterMapping);
                } else {
                    SwaggerDefinition swaggerDefinition = findDefinitionBySchema(swagger.getDefinitions(), swaggerParameter.getSchema());
                    if(swaggerDefinition != null) {
                        for(Map.Entry<String, SwaggerProperty> property : swaggerDefinition.getProperties().entrySet()) {
                            SwaggerParameter parameter = new SwaggerParameter();
                            parameter.setIn("body");
                            parameter.setName(property.getKey());
                            if(swaggerDefinition.getRequired() != null && swaggerDefinition.getRequired().contains(property.getKey())) {
                                parameter.setRequired(true);
                            } else {
                                parameter.setRequired(false);
                            }
                            parameter.setDescription(property.getKey() + " of: " +swaggerParameter.getDescription());
                            ParameterMapping parameterMapping = new ParameterMapping();
                            parameterMapping.setParameter(parameter);
                            parameterMapping.setRequired(parameter.isRequired());
                            contentParameterMappings.add(parameterMapping);
                        }
                    }
                }
            }
            operation.setParameterMappings(parameterMappings);
            operation.setContentParameterMappings(contentParameterMappings);
            view.addAvailableOperation(operation);
        }
    }

    private SwaggerDefinition findDefinitionBySchema(Map<String, SwaggerDefinition> definitions, SwaggerSchema schema) {
        if(definitions != null && schema != null && schema.get$ref() != null) {
            String[] refParts = schema.get$ref().split("/");
            for(String definitionName : definitions.keySet()) {
                if(definitionName.equals(refParts[refParts.length - 1])) {
                    return definitions.get(definitionName);
                }
            }
        }

        return null;
    }
}
