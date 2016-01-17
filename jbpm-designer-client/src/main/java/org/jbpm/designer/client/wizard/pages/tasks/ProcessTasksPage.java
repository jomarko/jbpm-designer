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
import javax.inject.Inject;
import java.util.*;

@Dependent
public class ProcessTasksPage implements WizardPage, ProcessTasksPageView.Presenter {

    @Inject
    ProcessTasksPageView view;

    @Inject
    private ClientUserSystemManager manager;

    private GuidedProcessWizard wizard;

    protected Map<Integer, Condition> conditions = new HashMap<Integer, Condition>();

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
            List<Task> tasks = view.getTasks(row);
            if(isRowCondition(row)) {
                conditions.remove(tasks.get(0).getId());
                conditions.remove(tasks.get(1).getId());
            }
            if(isRowParallel(row) || isRowCondition(row)) {
                view.setRowType(row, "");
                view.splitRow(row);
            }
        }
        view.hideMergeSplitButtons();
        view.hideConditionWidget();

        firePageChangedEvent();
    }

    @Override
    public void mergeTasksCondition() {
        List<Integer> selectedRows = view.getSelectedRows();
        if(selectedRows.size() == 2) {
            int newRow = view.mergeRows(selectedRows);
            view.setRowType(newRow, "condition");
            view.deselectAllRows();
            List<Task> tasks = view.getTasks(newRow);
            Condition con = new Condition();
            con.setConstraint(new Constraint());
            con.setPositiveTaskId(tasks.get(0).getId());
            con.setNegativeTaskId(tasks.get(1).getId());
            conditions.put(tasks.get(0).getId(), con);
            conditions.put(tasks.get(1).getId(), con);
        } else {
            view.showInvalidRowCountSelectedForCondition();
        }
        view.hideMergeSplitButtons();
        view.hideConditionWidget();
        firePageChangedEvent();
    }

    @Override
    public void mergeTasksParallel() {
        List<Integer> selectedRows = view.getSelectedRows();
        if(selectedRows.size() > 1) {
            int newRow = view.mergeRows(selectedRows);
            view.setRowType(newRow, "parallel");
            view.deselectAllRows();
        }
        view.hideMergeSplitButtons();
        view.hideConditionWidget();
        firePageChangedEvent();
    }

    @Override
    public void rowDeleted() {
        view.hideTaskDetail();
        view.deselectAllRows();
        view.hideMergeSplitButtons();
        view.hideConditionWidget();

        Set<Integer> existingKeys = new HashSet<Integer>();
        for(Task task : view.getTasks()) {
            existingKeys.add(task.getId());
        }

        for(Iterator<Map.Entry<Integer, Condition>> it = conditions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Condition> entry = it.next();
            if(!existingKeys.contains(entry.getKey())) {
                it.remove();
            }
        }
        firePageChangedEvent();
    }

    @Override
    public void rowSelected() {
        view.showTaskDetail();
        view.unbindAllWidgets();

        Task model = view.getModelOfSelectedWidget();

        if (view.getSelectedRows().size() == 1) {
            int row = view.getSelectedRows().get(0);
            if(isRowParallel(row) || isRowCondition(row)) {
                view.showSplitButton();
                if (isRowCondition(row)) {
                    view.showConditionWidget();
                    List<Task> tasks = view.getTasks(row);
                    tasks.remove(model);
                    Condition condition = conditions.get(model.getId());
                    view.setConditionModel(condition);
                    if(condition.getPositiveTaskId() == model.getId()) {
                        view.showConditionAsPositive();
                    } else {
                        view.showConditionAsNegative();
                    }
                }
            } else {
                view.hideMergeSplitButtons();
                Task task = view.getTasks(row).get(0);
                if(task.getTaskType() == Task.HUMAN_TYPE) {
                    view.showHumanSpecificDetails();
                }
                if(task.getTaskType() == Task.SERVICE_TYPE) {
                    view.showServiceSpecificDetails();
                }
            }

            if(!isRowCondition(row)) {
                view.hideConditionWidget();
            }

        } else if (view.getSelectedRows().size() > 1) {
            view.hideConditionWidget();
            boolean isThereParallelOrCondition = false;
            for (Integer rowIterator : view.getSelectedRows()) {
                if (isRowCondition(rowIterator) || isRowParallel(rowIterator)) {
                    isThereParallelOrCondition = true;
                    view.hideMergeSplitButtons();
                    break;
                }
            }

            if (!isThereParallelOrCondition) {
                view.showMergeButtons();
            }
        } else {
            view.hideConditionWidget();
            view.hideMergeSplitButtons();
        }

        view.setAvailableVarsForSelectedTask(getVariablesForTask(model));
        view.setModelForSelectedWidget(model);

        view.rebindSelectedWidget();
        view.highlightSelectedRows();
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

        if(conditions.containsKey(task.getId())) {
            if(!isConstraintValid(conditions.get(task.getId()).getConstraint())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isConstraintValid(Constraint constraint) {
        if(constraint == null) {
            return false;
        }

        if(constraint.getVariable() == null) {
            return false;
        }

        if(constraint.getConstraint() == null || constraint.getConstraint().isEmpty()) {
            return false;
        }

        if(constraint.getConstraintValue() == null || constraint.getConstraintValue().isEmpty()) {
            return false;
        }

        return true;
    }
}
