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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jbpm.designer.client.wizard.pages.widget.*;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.Operation;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class ProcessTasksPageViewImpl extends Composite implements ProcessTasksPageView, DeletableFlexTable.RowsHandler {

    interface ProcessTasksPageViewImplBinder
            extends
            UiBinder<Widget, ProcessTasksPageViewImpl> {
    }

    private static ProcessTasksPageViewImplBinder uiBinder = GWT.create(ProcessTasksPageViewImplBinder.class);

    private List<Widget> lastSelectedWidgets;

    private Presenter presenter;

    @Inject
    public ProcessTasksPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    TabPane taskDetailPane;

    @Inject
    protected TaskDetail taskDetail;

    @UiField
    TabPane taskIoPane;

    @Inject
    protected TaskIO taskIO;

    @UiField
    ConditionWidget conditionWidget;

    @UiField
    TasksTable tasksContainer;;

    @UiField
    Button parallelButton;

    @UiField
    Button conditionButton;

    @UiField
    Button splitButton;

    @UiField
    TabPanel conditionPanel;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        taskIoPane.add(taskIO);
        taskDetailPane.add(taskDetail);

        taskDetail.init(presenter);
        tasksContainer.registerRowsHandler(this);
        taskIO.setPropertyChangeChandler(getHandler());
        taskDetail.setPropertyChangeHandler(getHandler());
        conditionWidget.setPropertyChangeHandler(getHandler());

        tasksContainer.clear();
        lastSelectedWidgets = new ArrayList<Widget>();
    }

    @Override
    public void addedRow(List<Widget> widgets) {
        if(widgets.get(1) instanceof ListTaskDetail) {
            final ListTaskDetail detail = (ListTaskDetail) widgets.get(1);
            if(!detail.isInitialized()) {
                detail.setModel(presenter.getDefaultModel());
                detail.addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        if (!clickEvent.isControlKeyDown()) {
                            lastSelectedWidgets.clear();
                        }
                        addToLastSelected(detail);
                        presenter.taskDetailSelected(detail);
                    }
                }, ClickEvent.getType());
                detail.setInitialized(true);
            }
        }
    }

    @Override
    public void rowDeleted() {
        presenter.rowDeleted();
    }

    @Override
    public void addAvailableHumanParticipants(List<User> users) {
        taskDetail.addHumanParticipants(users);
    }

    @Override
    public void addAvailableGroupParticipants(List<Group> groups) {
        taskDetail.addGroupParticipants(groups);
    }

    @Override
    public List<Task> getTasks(int row) {
        return tasksContainer.getRowModels(row);
    }

    @Override
    public List<Widget> getWidgets(int row) {
        return tasksContainer.getRowWidgets(row);
    }

    @Override
    public int getRowsCount() {
        return tasksContainer.getRowCount();
    }

    @UiHandler("addButton")
    public void addButtonHandler(ClickEvent event) {
        tasksContainer.addNewRow(tasksContainer.getNewRowWidgets());
    }

    @UiHandler("parallelButton")
    public void parallelButtonHandler(ClickEvent event) {
        presenter.mergeTasks(false);
    }

    @UiHandler("conditionButton")
    public void conditionButtonHandler(ClickEvent event) {
        presenter.mergeTasks(true);
    }

    @UiHandler("splitButton")
    public void splitButtonHandler(ClickEvent event) {
        presenter.splitTasks();
    }

    @Override
    public List<Widget> getSelectedWidgets() {
        return lastSelectedWidgets;
    }

    @Override
    public void mergeSelectedWidgets() {
        if(lastSelectedWidgets != null && lastSelectedWidgets.size() == 2) {
            int rowOfFirst = tasksContainer.getRowOfWidget(lastSelectedWidgets.get(0));
            int rowOfSecond = tasksContainer.getRowOfWidget(lastSelectedWidgets.get(1));
            if(rowOfFirst != rowOfSecond) {
                tasksContainer.addWidgetToEnd(rowOfFirst, lastSelectedWidgets.get(1));
                if(tasksContainer.getRowWidgets(rowOfFirst).get(0) instanceof MergedTasksIndicator) {
                    MergedTasksIndicator indicator = (MergedTasksIndicator) tasksContainer.getRowWidgets(rowOfFirst).get(0);
                    indicator.setVisible(true);
                }
                tasksContainer.removeRow(rowOfSecond);
            }
        }
    }

    @Override
    public void splitSelectedWidgets() {
        if(lastSelectedWidgets.size() == 2) {
            int rowOfFirst = tasksContainer.getRowOfWidget(lastSelectedWidgets.get(0));
            int rowOfSecond = tasksContainer.getRowOfWidget(lastSelectedWidgets.get(1));
            if(rowOfFirst == rowOfSecond) {
                tasksContainer.split(rowOfFirst);
            }
        }
    }

    @Override
    public void deselectAll() {
        lastSelectedWidgets.clear();
        tasksContainer.highlightWidgets(lastSelectedWidgets);
    }

    @Override
    public void highlightSelected() {
        tasksContainer.highlightWidgets(lastSelectedWidgets);
    }

    @Override
    public void showHumanSpecificDetails() {
        taskDetail.showHumanDetails();
    }

    @Override
    public void showServiceSpecificDetails() {
        taskDetail.showServiceDetails();
    }

    @Override
    public void unbindAllTaskWidgets() {
        for(int row = 0; row < tasksContainer.getRowCount(); row++) {
            for (Widget widget : tasksContainer.getRowWidgets(row)) {
                if(widget != null) {
                    if (widget instanceof ListTaskDetail) {
                        ((ListTaskDetail) widget).unbind();
                    }
                }
            }
        }

        taskDetail.unbind();
    }

    @Override
    public void rebindTaskDetailWidgets() {
        taskDetail.rebind();
    }

    @Override
    public void setModelTaskDetailWidgets(Task model) {
        taskDetail.setModel(model);
        taskIO.setModel(model);
    }

    @Override
    public void setAvailableVarsForSelectedTask(List<Variable> variables) {
        conditionWidget.setVariables(variables);
        taskIO.setAcceptableValues(variables);
        taskDetail.setVariablesForParameterMapping(variables);
    }

    @Override
    public void rebindConditionWidgetToModel(Condition model) {
        conditionWidget.unbind();
        conditionWidget.setModel(model);
        conditionWidget.rebind();
    }

    @Override
    public void showSplitInvalidCount() {
        Window.alert("You can split only one row");
    }

    @Override
    public void showMergeInvalidCount() {
        Window.alert("Merge supported only for 2 tasks");
    }

    @Override
    public void showAlreadyContainsMerged() {
        Window.alert("Can't merge already merged tasks");
    }

    @Override
    public void showAsValid(int taskId) {
        int row = rowOfTask(taskId);
        tasksContainer.setNormalColor(row, columnOfTask(row, taskId));
    }

    @Override
    public void showAsInvalid(int taskId) {
        int row = rowOfTask(taskId);
        tasksContainer.setRedColor(row, columnOfTask(row, taskId));
    }

    @Override
    public void setNameHelpVisibility(boolean value) {
        taskDetail.setNameHelpVisibility(value);
    }

    @Override
    public void setParticipantHelpVisibility(boolean value) {
        taskDetail.setParticipantHelpVisibility(value);
    }

    @Override
    public void setOperationHelpVisibility(boolean value) {
        taskDetail.setOperationHelpVisibility(value);
    }

    @Override
    public void setVariableHelpVisibility(boolean value) {
        conditionWidget.setVariableHelpVisibility(value);
    }

    @Override
    public void setConstraintValueHelpVisibility(boolean value) {
        conditionWidget.setConstraintValueHelpVisibility(value);
    }

    @Override
    public void setConstraintHelpVisibility(boolean value) {
        conditionWidget.setConstraintHelpVisibility(value);
    }

    @Override
    public void setMergeButtonsVisibility(boolean value) {
        parallelButton.setVisible(value);
        conditionButton.setVisible(value);
    }

    @Override
    public void setSplitButtonVisibility(boolean value) {
        splitButton.setVisible(value);
    }

    @Override
    public void setConditionPanelVisibility(boolean value) {
        conditionPanel.setVisible(value);
    }

    @Override
    public void addAvailableOperation(Operation operation) {
        taskDetail.addAvailableOperation(operation);
    }

    private PropertyChangeHandler getHandler() {
        return new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                presenter.firePageChangedEvent();
            }
        };
    }


    private int rowOfTask(int taskId) {
        for(int row = 0; row < tasksContainer.getRowCount(); row++) {
            for(Task t : tasksContainer.getRowModels(row)) {
                if(t.getId() == taskId) {
                    return row;
                }
            }
        }

        return 0;
    }

    private int columnOfTask(int row, int taskId) {
        int column = 0;
        for(Task t : tasksContainer.getRowModels(row)) {
            if (t.getId() == taskId) {
                return column + 1;
            }
            column++;
        }

        return 0;
    }

    private void addToLastSelected(Widget widget) {
        if(lastSelectedWidgets.contains(widget)) {
            lastSelectedWidgets.remove(widget);
        } else {
            lastSelectedWidgets.add(widget);
        }
    }
}
