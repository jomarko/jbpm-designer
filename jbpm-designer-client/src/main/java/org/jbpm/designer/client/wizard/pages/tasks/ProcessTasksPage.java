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
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.*;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
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
        for(Task task : view.getTasks()) {
            if(!isTaskValid(task)) {
                allTasksValid = false;
                view.showAsInvalid(task.getId());
            } else {
                view.showAsValid(task.getId());
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
        output.setDataType("string");
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
    public boolean isRowParallel(int row) {
        return "parallel".compareTo(view.getRowType(row)) == 0;
    }

    @Override
    public boolean isRowCondition(int row) {
        return "condition".compareTo(view.getRowType(row)) == 0;
    }

    @Override
    public void splitTasks() {
        List<Integer> selectedRows = view.getSelectedRows();
        if(selectedRows.size() == 1) {
            int row = selectedRows.get(0);
            if(isRowParallel(row) || isRowCondition(row)) {
                view.setRowType(row, "");
                view.splitRow(row);
                view.deselectAll();
                firePageChangedEvent();
            }
        } else {
            view.showSplitInvalidCount();
        }

    }

    @Override
    public void mergeTasksCondition() {
        List<Integer> selectedRows = view.getSelectedRows();
        if(selectedRows.size() == 2) {
            if(canBeMerged(selectedRows)) {
                int newRow = view.mergeRows(selectedRows);
                view.setRowType(newRow, "condition");
                view.deselectAll();
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
        List<Integer> selectedRows = view.getSelectedRows();
        if(selectedRows.size() == 2) {
            if(canBeMerged(selectedRows)) {
                int newRow = view.mergeRows(selectedRows);
                view.setRowType(newRow, "parallel");
                view.deselectAll();
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
        Set<Integer> existingKeys = new HashSet<Integer>();
        for(Task task : view.getTasks()) {
            existingKeys.add(task.getId());
        }

        view.deselectAll();
        firePageChangedEvent();
    }

    @Override
    public void startSelection() {
        view.showButtonsAfterSelection();
    }

    @Override
    public void cancelSelection() {
        view.showButtonsAfterSelectionCancel();
        view.deselectAll();
        firePageChangedEvent();
    }

    @Override
    public void rowSelected() {
        view.unbindAllWidgets();

        Task model = view.getModelOfSelectedWidget();

        if(model.getTaskType() == Task.HUMAN_TYPE) {
            view.showHumanSpecificDetails();
        }
        if(model.getTaskType() == Task.SERVICE_TYPE) {
            view.showServiceSpecificDetails();
        }

        view.setAvailableVarsForSelectedTask(getVariablesForTask(model));
        view.setModelForSelectedWidget(model);

        view.rebindSelectedWidget();
        view.highlightSelected();
        firePageChangedEvent();
    }

    public List<Variable> getVariablesForTask(Task task) {
        List<Variable> result = wizard.getInitialInputs();
        return result;
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

    public void showHelpForSelectedTask(@Observes WizardPageStatusChangeEvent event) {
        Task task = view.getModelOfSelectedWidget();

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

    private boolean canBeMerged(List<Integer> rows) {
        for(Integer row : rows) {
            if(isRowCondition(row) || isRowParallel(row)) {
                return false;
            }
        }

        return true;
    }
}
