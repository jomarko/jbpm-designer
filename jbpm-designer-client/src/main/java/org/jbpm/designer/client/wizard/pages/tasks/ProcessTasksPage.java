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
package org.jbpm.designer.client.wizard.pages.tasks;/**/

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.jbpm.designer.client.wizard.pages.widget.ConstraintHolder;
import org.jbpm.designer.client.wizard.pages.widget.ListTaskDetail;
import org.jbpm.designer.client.wizard.pages.widget.TasksHolder;
import org.jbpm.designer.model.*;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class ProcessTasksPage implements WizardPage, ProcessTasksPageView.Presenter {

    @Inject
    ProcessTasksPageView view;

    @Inject
    private ClientUserSystemManager manager;

    private GuidedProcessWizard wizard;

    private WizardPageStatusChangeEvent pageChanged = new WizardPageStatusChangeEvent(this);

    @Inject
    private Event<WizardPageStatusChangeEvent> event;

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
    }

    @Override
    public void prepareView() {
        manager.users(new RemoteCallback<AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.identity.User>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.identity.User> response) {
                List<User> availableUsers = new ArrayList<User>();
                for(org.jboss.errai.security.shared.api.identity.User u : response.getResults()) {
                    availableUsers.add(new User(u.getIdentifier()));
                }
                view.setAvailableHumanParticipants(availableUsers);
            }
        }, new ErrorCallback() {
            @Override
            public boolean error(Object o, Throwable throwable) {
                Window.alert("<!>" + throwable.getMessage());
                return false;
            }
        }).search(new SearchRequestImpl("", 1, 10));

        manager.groups(new RemoteCallback<AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.Group>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<org.jboss.errai.security.shared.api.Group> response) {
                List<Group> availableGroups = new ArrayList<Group>();
                for(org.jboss.errai.security.shared.api.Group g : response.getResults()) {
                    availableGroups.add(new Group(g.getName()));
                }
                view.setAvailableGroupParticipants(availableGroups);
            }
        }, new ErrorCallback() {
            @Override
            public boolean error(Object o, Throwable throwable) {
                Window.alert("<!>" + throwable.getMessage());
                return false;
            }
        }).search(new SearchRequestImpl("", 1, 10));
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
        Task task = new Task("");
        task.setTaskType(Task.HUMAN_TYPE);
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
        if(selectedWidgets.size() == 1) {
            if(selectedWidgets.get(0) instanceof TasksHolder) {
                ((TasksHolder) selectedWidgets.get(0)).removeHandlers();
                view.split((TasksHolder)selectedWidgets.get(0));
                view.deselectAll();
                view.setSplitButtonVisibility(false);
                firePageChangedEvent();
            }
        } else {
            view.showSplitInvalidCount();
        }
    }

    @Override
    public void mergeTasksCondition() {
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets.size() == 2) {
            if(canBeMerged(selectedWidgets)) {
                List<ListTaskDetail> details = new ArrayList<ListTaskDetail>();
                details.add((ListTaskDetail) selectedWidgets.get(0));
                details.add((ListTaskDetail) selectedWidgets.get(1));
                view.mergeCondition(details);
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
    public void mergeTasksParallel() {
        List<Widget> selectedWidgets = view.getSelectedWidgets();
        if(selectedWidgets.size() == 2) {
            if(canBeMerged(selectedWidgets)) {
                List<ListTaskDetail> details = new ArrayList<ListTaskDetail>();
                details.add((ListTaskDetail) selectedWidgets.get(0));
                details.add((ListTaskDetail) selectedWidgets.get(1));
                view.mergeParallel(details);
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
        firePageChangedEvent();
    }

    @Override
    public void taskDetailSelected(ListTaskDetail detail) {
        view.unbindAllWidgets();

        Task model = detail.getModel();
        if(model.getTaskType() == Task.HUMAN_TYPE) {
            view.showHumanSpecificDetails();
        }
        if(model.getTaskType() == Task.SERVICE_TYPE) {
            view.showServiceSpecificDetails();
        }

        view.setAvailableVarsForSelectedTask(getVariablesForTask(model));
        detail.setModel(model);
        view.setModelForSelectedWidget(model);

        detail.rebind();
        view.rebindSelectedWidget();
        view.highlightSelected();
        if( view.getSelectedWidgets().size() > 1 ) {
            view.setMergeButtonsVisibility(true);
            view.setSplitButtonVisibility(false);
        } else {
            view.setMergeButtonsVisibility(false);
            if(view.getSelectedWidgets().size() == 1 && view.getSelectedWidgets().get(0) instanceof TasksHolder) {
                view.setSplitButtonVisibility(true);
            } else {
                view.setSplitButtonVisibility(false);
            }
        }
        firePageChangedEvent();
    }

    @Override
    public void holderSelected(TasksHolder holder) {
        view.selectAllWidgetsOfHolder(holder);
        view.highlightSelected();
        view.setSplitButtonVisibility(true);
        if(holder instanceof ConstraintHolder) {
            if (holder.getTasks() != null) {
                for (ListTaskDetail detail : holder.getTasks()) {
                    detail.getModel().getCondition().setConstraint(((ConstraintHolder)holder).getModel());
                }
            }
        }
    }

    public List<Variable> getVariablesForTask(Task task) {
        List<Variable> possibleInputs = wizard.getInitialInputs();
        for(Map.Entry<Integer, List<Task>> tasksGroup : getTasks().entrySet()) {
            for(Task previousTask : tasksGroup.getValue()) {
                if(task != previousTask) {
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

        if(task.getTaskType() == Task.SERVICE_TYPE) {
            if(task.getOperation() == null || task.getOperation().isEmpty()) {
                return false;
            }
        }

        if(task.getTaskType() == Task.HUMAN_TYPE) {
            if(task.getResponsibleHuman() == null && task.getResponsibleGroup() == null) {
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

    public Set<Integer> getConditionBasedGroups() {
        return view.getConditionBasedGroups();
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

            if (task.getTaskType() == Task.HUMAN_TYPE) {
                if (task.getResponsibleHuman() == null && task.getResponsibleGroup() == null) {
                    view.setParticipantHelpVisibility(true);
                } else {
                    view.setParticipantHelpVisibility(false);
                }
            }

            if (task.getTaskType() == Task.SERVICE_TYPE) {
                if (task.getOperation() == null || task.getOperation().isEmpty()) {
                    view.setOperationHelpVisibility(true);
                } else {
                    view.setOperationHelpVisibility(false);
                }
            }
        }
    }

    private boolean canBeMerged(List<Widget> widgets) {
        for(Widget widget : widgets) {
            if(!(widget instanceof  ListTaskDetail)) {
                return false;
            }
        }

        return true;
    }
}
