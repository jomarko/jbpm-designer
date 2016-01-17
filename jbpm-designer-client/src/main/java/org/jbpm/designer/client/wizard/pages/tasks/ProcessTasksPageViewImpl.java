package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jbpm.designer.client.shared.*;
import org.jbpm.designer.client.wizard.pages.widget.*;

import javax.annotation.PostConstruct;
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

    private List<Integer> lastSelectedRows = new ArrayList<Integer>();

    private Presenter presenter;

    private ListTaskDetail lastSelectedWidget;

    @Inject
    public ProcessTasksPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Inject
    private TaskDetail taskDetail;

    @Inject
    private ConditionWidget conditionWidget;

    @UiField
    VerticalPanel detail;

    @UiField
    TasksTable tasksContainer;

    @UiField
    Button parallelButton;

    @UiField
    Button conditionButton;

    @UiField
    Button splitButton;


    @PostConstruct
    public void initView() {
        detail.add(parallelButton);
        detail.add(taskDetail);
        detail.add(conditionWidget);
        tasksContainer.registerRowsHandler(this);
        taskDetail.setPropertyChangeChandler(getHandler());
        conditionWidget.setPropertyChangeHandler(getHandler());
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        taskDetail.setVisible(false);
        conditionWidget.setVisible(false);
        tasksContainer.clear();
        lastSelectedRows = new ArrayList<Integer>();
    }

    @Override
    public void addedRow(Widget widget) {
        ((ListTaskDetail) widget).setModel(presenter.getDefaultModel());
    }

    @Override
    public void rowSelected(Widget widget, Integer row, boolean ctrlPressed) {
        lastSelectedWidget = (ListTaskDetail) widget;
        if (ctrlPressed) {
            addToLastSelected(row);
        } else {
            lastSelectedRows.clear();
            addToLastSelected(row);
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

    @Override
    public List<Integer> getSelectedRows() {
        return lastSelectedRows;
    }

    @Override
    public int mergeRows(List<Integer> rows) {
        return tasksContainer.merge(rows);
    }

    @Override
    public void deselectAllRows() {
        lastSelectedRows.clear();
        tasksContainer.highlightRows(lastSelectedRows);
    }

    @Override
    public void highlightSelectedRows() {
        tasksContainer.highlightRows(lastSelectedRows);
    }

    @Override
    public void hideMergeSplitButtons() {
        parallelButton.setVisible(false);
        conditionButton.setVisible(false);
        splitButton.setVisible(false);
    }

    @Override
    public void splitRow(int row) {
        tasksContainer.split(row);
    }

    @Override
    public void hideTaskDetail() {
        taskDetail.setVisible(false);
    }

    @Override
    public void showTaskDetail() {
        taskDetail.setVisible(true);
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
    }

    @Override
    public void rebindSelectedWidget() {
        lastSelectedWidget.rebind();
        taskDetail.rebind();
        if(!taskDetail.isVisible()) {
            taskDetail.setVisible(true);
        }
        conditionWidget.rebind();
    }

    @Override
    public void setModelForSelectedWidget(Task model) {
        lastSelectedWidget.setModel(model);
        taskDetail.setModel(model);
    }

    @Override
    public void setAvailableVarsForSelectedTask(List<Variable> variables) {
        taskDetail.setVariables(variables);
        conditionWidget.setVariables(variables);
    }

    @Override
    public Task getModelOfSelectedWidget() {
        return lastSelectedWidget.getModel();
    }

    @Override
    public void showMergeButtons() {
        parallelButton.setVisible(true);
        conditionButton.setVisible(true);
        splitButton.setVisible(false);
    }

    @Override
    public void showSplitButton() {
        parallelButton.setVisible(false);
        conditionButton.setVisible(false);
        splitButton.setVisible(true);
    }

    @Override
    public void showConditionWidget() {
        conditionWidget.setVisible(true);
    }

    @Override
    public void hideConditionWidget() {
        conditionWidget.setVisible(false);
    }

    @Override
    public void setConditionModel(Condition condition) {
        conditionWidget.setModel(condition);
    }

    @Override
    public void showConditionAsPositive() {
        conditionWidget.setConstraintSatisfied(true);
    }

    @Override
    public void showConditionAsNegative() {
        conditionWidget.setConstraintSatisfied(false);
    }

    @Override
    public void showInvalidRowCountSelectedForCondition() {
        Window.alert("Conditions supported only for 2 tasks");
    }

    @Override
    public void showAsValid(int taskId) {
        tasksContainer.setNormalRowColor(rowOfTask(taskId));
    }

    @Override
    public void showAsInvalid(int taskId) {
        tasksContainer.setRedRowColor(rowOfTask(taskId));
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

    private void addToLastSelected(Integer row) {
        if(lastSelectedRows.contains(row)) {
            lastSelectedRows.remove(row);
        }

        lastSelectedRows.add(row);
    }
}
