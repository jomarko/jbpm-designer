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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jbpm.designer.client.shared.*;
import org.jbpm.designer.client.wizard.pages.widget.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
public class ProcessTasksPageViewImpl extends Composite implements ProcessTasksPageView, DeletableFlexTable.RowsHandler {

    interface ProcessTasksPageViewImplBinder
            extends
            UiBinder<Widget, ProcessTasksPageViewImpl> {
    }

    private static ProcessTasksPageViewImplBinder uiBinder = GWT.create(ProcessTasksPageViewImplBinder.class);

    private boolean isSelectingActive;

    private Map<Integer, List<Integer>> lastSelectedCells;

    private Presenter presenter;

    private ListTaskDetail lastSelectedWidget;

    @Inject
    public ProcessTasksPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    TaskDetail taskDetail;

    @UiField
    TaskIO taskIO;

    @UiField
    ConditionWidget conditionWidget;

    @UiField
    TasksTable tasksContainer;

    @UiField
    Button selectButton;

    @UiField
    Button cancelButton;

    @UiField
    Button parallelButton;

    @UiField
    Button conditionButton;

    @UiField
    Button splitButton;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;

        tasksContainer.registerRowsHandler(this);
        taskDetail.setPropertyChangeChandler(getHandler());
        conditionWidget.setPropertyChangeHandler(getHandler());

        tasksContainer.clear();
        isSelectingActive = false;
        lastSelectedCells = new HashMap<Integer, List<Integer>>();
    }

    @Override
    public void addedRow(Widget widget) {
        ((ListTaskDetail) widget).setModel(presenter.getDefaultModel());
    }

    @Override
    public void cellSelected(Widget widget, Integer row, Integer column) {
        lastSelectedWidget = (ListTaskDetail) widget;
        if (isSelectingActive) {
            addToLastSelected(row, column);
        } else {
            lastSelectedCells.clear();
            addToLastSelected(row, column);
        }
        presenter.rowSelected();
    }

    @Override
    public void rowDeleted() {
        presenter.rowDeleted();
    }

    @Override
    public void setAvailableHumanParticipants(List<User> users) {
        taskDetail.setHumanParticipants(users);
    }

    @Override
    public void setAvailableGroupParticipants(List<Group> groups) {
        taskDetail.setGroupParticipants(groups);
    }

    @Override
    public List<Task> getTasks() {
        return tasksContainer.getModels();
    }

    @Override
    public List<Task> getTasks(int row) {
        return tasksContainer.getRowModels(row);
    }

    @Override
    public String getRowType(int row) {
        return tasksContainer.getRowId(row);
    }

    @Override
    public void setRowType(int row, String type) {
        tasksContainer.setRowId(row, type);
    }

    @UiHandler("addButton")
    public void addButtonHandler(ClickEvent event) {
        tasksContainer.addNewRow();
    }

    @UiHandler("parallelButton")
    public void parallelButtonHandler(ClickEvent event) {
        presenter.mergeTasksParallel();
    }

    @UiHandler("conditionButton")
    public void conditionButtonHandler(ClickEvent event) {
        presenter.mergeTasksCondition();
    }

    @UiHandler("splitButton")
    public void splitButtonHandler(ClickEvent event) {
        presenter.splitTasks();
    }

    @UiHandler("selectButton")
    public void selectButtonHandler(ClickEvent event) {
        isSelectingActive = true;
        presenter.startSelection();
    }

    @UiHandler("cancelButton")
    public void cancelButtonHandler(ClickEvent event) {
        isSelectingActive = false;
        presenter.cancelSelection();
    }

    @Override
    public List<Integer> getSelectedRows() {
        return new ArrayList<Integer>(lastSelectedCells.keySet());
    }

    @Override
    public int mergeRows(List<Integer> rows) {
        return tasksContainer.merge(rows);
    }

    @Override
    public void deselectAll() {
        lastSelectedCells.clear();
        tasksContainer.highlightCells(lastSelectedCells);
    }

    @Override
    public void highlightSelected() {
        tasksContainer.highlightCells(lastSelectedCells);
    }

    @Override
    public void splitRow(int row) {
        tasksContainer.split(row);
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
    public void unbindAllWidgets() {
        for(int row = 0; row < tasksContainer.getRowCount(); row++) {
            for (ListTaskDetail keyWidget : tasksContainer.getRowWidgets(row)) {
                keyWidget.unbind();
            }
        }

        taskDetail.unbind();
        conditionWidget.unbind();
        taskIO.unbind();
    }

    @Override
    public void rebindSelectedWidget() {
        lastSelectedWidget.rebind();
        taskDetail.rebind();
        conditionWidget.rebind();
        taskIO.rebind();
    }

    @Override
    public void setModelForSelectedWidget(Task model) {
        lastSelectedWidget.setModel(model);
        taskDetail.setModel(model);
        conditionWidget.setModel(model);
        taskIO.setModel(model);
    }

    @Override
    public void setAvailableVarsForSelectedTask(List<Variable> variables) {
        conditionWidget.setVariables(variables);
        taskIO.setAcceptableValues(variables);
    }

    @Override
    public Task getModelOfSelectedWidget() {
        if(lastSelectedWidget != null) {
            return lastSelectedWidget.getModel();
        } else {
            return null;
        }
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
        tasksContainer.setNormalRowColor(row, columnOfTask(row, taskId));
    }

    @Override
    public void showAsInvalid(int taskId) {
        int row = rowOfTask(taskId);
        tasksContainer.setRedRowColor(row, columnOfTask(row, taskId));
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
    public void setSelectedTaskInputs(List<Variable> variables) {
        taskIO.setSelectedVariables(variables);
    }

    @Override
    public void setSelectedTaskOutput(Variable variable) {
        taskIO.setOutputVariable(variable);
    }

    @Override
    public void showButtonsAfterSelection() {
        selectButton.setVisible(false);
        splitButton.setVisible(true);
        parallelButton.setVisible(true);
        conditionButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    @Override
    public void showButtonsAfterSelectionCancel() {
        selectButton.setVisible(true);
        splitButton.setVisible(false);
        parallelButton.setVisible(false);
        conditionButton.setVisible(false);
        cancelButton.setVisible(false);
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
                return column;
            }
            column++;
        }

        return 0;
    }

    private void addToLastSelected(Integer row, Integer column) {
        if(!lastSelectedCells.keySet().contains(row)) {
            lastSelectedCells.put(row, new ArrayList<Integer>());
        }
        List<Integer> columns = lastSelectedCells.get(row);
        if(!columns.contains(column)) {
            columns.add(column);
            lastSelectedCells.put(row, columns);
        } else {
            columns.remove(column);
            lastSelectedCells.put(row, columns);
        }
    }
}
