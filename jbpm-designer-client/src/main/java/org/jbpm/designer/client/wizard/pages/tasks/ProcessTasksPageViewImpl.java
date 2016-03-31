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
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jbpm.designer.client.wizard.pages.widget.*;
import org.jbpm.designer.model.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Dependent
public class ProcessTasksPageViewImpl extends Composite implements ProcessTasksPageView, DeletableFlexTable.RowsHandler {

    interface ProcessTasksPageViewImplBinder
            extends
            UiBinder<Widget, ProcessTasksPageViewImpl> {
    }

    private static ProcessTasksPageViewImplBinder uiBinder = GWT.create(ProcessTasksPageViewImplBinder.class);

    private boolean isSelectingActive;

    private List<Widget> lastSelectedWidgets;

    private Presenter presenter;

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

        taskDetail.init(this.presenter);
        conditionWidget.init(this.presenter);
        taskIO.init(this.presenter);

        tasksContainer.registerRowsHandler(this);
        taskDetail.setPropertyChangeChandler(getHandler());
        conditionWidget.setPropertyChangeHandler(getHandler());

        tasksContainer.clear();
        isSelectingActive = false;
        lastSelectedWidgets = new ArrayList<Widget>();
    }

    @Override
    public void addedRow(final Widget widget) {
        ((ListTaskDetail) widget).setModel(presenter.getDefaultModel());
        widget.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (isSelectingActive) {
                    addToLastSelected(widget);
                } else {
                    lastSelectedWidgets.clear();
                    addToLastSelected(widget);
                }
                presenter.taskDetailSelected((ListTaskDetail)widget);
            }
        }, ClickEvent.getType());
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
    public List<Task> getTasks(int row) {
        return tasksContainer.getRowModels(row);
    }

    @Override
    public int getRowsCount() {
        return tasksContainer.getRowCount();
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
    public List<Widget> getSelectedWidgets() {
        return lastSelectedWidgets;
    }

    @Override
    public void mergeParallel(List<ListTaskDetail> widgets) {
        List<Integer> rows = new ArrayList<Integer>();
        for(Widget widget : widgets) {
            rows.add(tasksContainer.getRowOfWidget(widget));
        }

        Collections.sort(rows);

        for(int i = 1; i < rows.size(); i++) {
            tasksContainer.removeRow(rows.get(i) + i - 1);
        }

        final ParallelHolder holder = new ParallelHolder(presenter, widgets);
        tasksContainer.setWidget(rows.get(0), 0, holder);
    }

    @Override
    public void mergeCondition(List<ListTaskDetail> widgets) {
        List<Integer> rows = new ArrayList<Integer>();
        for(Widget widget : widgets) {
            rows.add(tasksContainer.getRowOfWidget(widget));
        }

        Collections.sort(rows);

        for(int i = 1; i < rows.size(); i++) {
            tasksContainer.removeRow(rows.get(i) + i - 1);
        }

        final ConstraintHolder holder = new ConstraintHolder(presenter, widgets);
        holder.setModel(new Constraint());
        tasksContainer.setWidget(rows.get(0), 0, holder);
    }

    @Override
    public void split(TasksHolder holder) {
        tasksContainer.split(holder);
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
    public void unbindAllWidgets() {
        for(int row = 0; row < tasksContainer.getRowCount(); row++) {
            for (Widget widget : tasksContainer.getRowWidgets(row)) {
                if(widget != null) {
                    if (widget instanceof ListTaskDetail) {
                        ((ListTaskDetail) widget).unbind();
                    }
                    if(widget instanceof ConstraintHolder) {
                        for(ListTaskDetail detail : ((ConstraintHolder)widget).getTasks()) {
                            detail.unbind();
                        }
                    }
                }
            }
        }

        taskDetail.unbind();
        conditionWidget.unbind();
        taskIO.unbind();
    }

    @Override
    public void rebindSelectedWidget() {
        taskDetail.rebind();
        conditionWidget.rebind();
        taskIO.rebind();
    }

    @Override
    public void setModelForSelectedWidget(Task model) {
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
        parallelButton.setVisible(true);
        conditionButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    @Override
    public void showButtonsAfterSelectionCancel() {
        selectButton.setVisible(true);
        parallelButton.setVisible(false);
        conditionButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    @Override
    public void setSplitButtonVisibility(boolean value) {
        splitButton.setVisible(value);
    }

    @Override
    public void selectAllWidgetsOfHolder(TasksHolder holder) {
        for(ListTaskDetail detail : holder.getTasks()) {
            lastSelectedWidgets.remove(detail);
        }

        lastSelectedWidgets.add(holder);
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

    private void addToLastSelected(Widget widget) {
        if(lastSelectedWidgets.contains(widget)) {
            lastSelectedWidgets.remove(widget);
        } else {
            for(Widget w : lastSelectedWidgets) {
                if(w instanceof TasksHolder) {
                    if(((TasksHolder) w).getTasks().contains(widget)) {
                        return;
                    }
                }
            }
            lastSelectedWidgets.add(widget);
        }
    }
}
