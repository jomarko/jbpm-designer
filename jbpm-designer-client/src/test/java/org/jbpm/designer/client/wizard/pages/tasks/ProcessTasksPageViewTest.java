package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.pages.widget.*;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.Operation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@WithClassesToStub(ValueListBox.class)
public class ProcessTasksPageViewTest {

    @Mock
    TaskDetail taskDetail;

    @Mock
    TaskIO taskIO;

    @Mock
    TabPane taskIoPane;

    @Mock
    TabPane taskDetailPane;

    @Mock
    TasksTable tasksContainer;

    @Mock
    ConditionWidget conditionWidget;

    @Mock
    Button conditionButton;

    @Mock
    Button parallelButton;

    @Mock
    Button splitButton;

    @Mock
    TabPanel conditionPanel;

    @Mock
    TabPanel taskDetailPanel;

    @Mock
    private ProcessTasksPageView.Presenter presenter;

    Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    ArgumentCaptor<ClickHandler> clickCaptor;

    @Captor
    ArgumentCaptor<NotificationEvent> notificationEvent;

    @Mock
    org.gwtbootstrap3.client.ui.ValueListBox<String> taskType;

    private ProcessTasksPageViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        view = GWT.create(ProcessTasksPageViewImpl.class);
        view.taskDetail = taskDetail;
        view.taskIO = taskIO;
        view.taskIoPane = taskIoPane;
        view.taskDetailPane = taskDetailPane;
        view.tasksContainer = tasksContainer;
        view.notification = notification;
        view.conditionWidget = conditionWidget;
        view.parallelButton = parallelButton;
        view.conditionButton = conditionButton;
        view.splitButton = splitButton;
        view.conditionPanel = conditionPanel;
        view.taskDetailPanel = taskDetailPanel;
        view.taskType = taskType;

        doCallRealMethod().when(view).init(any(ProcessTasksPageView.Presenter.class));
        doCallRealMethod().when(view).rebindConditionWidgetToModel(any(Condition.class));
        doCallRealMethod().when(view).rebindTaskDetailWidgets();
        doCallRealMethod().when(view).setParticipantHelpVisibility(anyBoolean());
        doCallRealMethod().when(view).setMergeButtonsVisibility(anyBoolean());
        doCallRealMethod().when(view).setVariableHelpVisibility(anyBoolean());
        doCallRealMethod().when(view).setSplitButtonVisibility(anyBoolean());
        doCallRealMethod().when(view).unbindAllTaskWidgets();
        doCallRealMethod().when(view).getTasks(anyInt());
        doCallRealMethod().when(view).getRowsCount();
        doCallRealMethod().when(view).setAcceptableOperations(anyList());
        doCallRealMethod().when(view).showMergeInvalidCount();
        doCallRealMethod().when(view).conditionButtonHandler(any(ClickEvent.class));
        doCallRealMethod().when(view).highlightSelected();
        doCallRealMethod().when(view).setTaskPanelVisibility(anyBoolean());
        doCallRealMethod().when(view).splitSelectedWidgets();
        doCallRealMethod().when(view).setAvailableDataTypes(anyList());
        doCallRealMethod().when(view).addedRow(anyList());
        doCallRealMethod().when(view).setConstraintHelpVisibility(anyBoolean());
        doCallRealMethod().when(view).setConstraintValueHelpVisibility(anyBoolean());
        doCallRealMethod().when(view).addButtonHandler(any(ClickEvent.class));
        doCallRealMethod().when(view).splitButtonHandler(any(ClickEvent.class));
        doCallRealMethod().when(view).showAlreadyContainsMerged();
        doCallRealMethod().when(view).addAvailableHumanParticipants(anyList());
        doCallRealMethod().when(view).addAvailableGroupParticipants(anyList());
        doCallRealMethod().when(view).showHumanSpecificDetails();
        doCallRealMethod().when(view).showServiceSpecificDetails();
        doCallRealMethod().when(view).getWidgets(anyInt());
        doCallRealMethod().when(view).mergeSelectedWidgets(anyBoolean());
        doCallRealMethod().when(view).setModelTaskDetailWidgets(any(Task.class));
        doCallRealMethod().when(view).setNameHelpVisibility(anyBoolean());
        doCallRealMethod().when(view).setConditionPanelVisibility(anyBoolean());
        doCallRealMethod().when(view).setOperationHelpVisibility(anyBoolean());
        doCallRealMethod().when(view).deselectAll();
        doCallRealMethod().when(view).showSplitInvalidCount();
        doCallRealMethod().when(view).rowDeleted();
        doCallRealMethod().when(view).parallelButtonHandler(any(ClickEvent.class));

        view.init(presenter);
    }

    @Test
    public void testInit() throws Exception {
        verify(taskIoPane).add(taskIO);
        verify(taskDetailPane).add(taskDetail);
        verify(taskDetail).init(presenter);
        verify(tasksContainer).clear();
    }

    @Test
    public void testRowDeleted() {
        view.rowDeleted();
        verify(presenter).rowDeleted();
    }

    @Test
    public void testAddAvailableHumanParticipants() {
        List<User> users = new ArrayList<User>();
        users.add(mock(User.class));
        users.add(mock(User.class));
        view.addAvailableHumanParticipants(users);
        verify(taskDetail).addHumanParticipants(users);
    }

    @Test
    public void testAddAvailableGroupParticipants() {
        List<Group> groups = new ArrayList<Group>();
        groups.add(mock(Group.class));
        groups.add(mock(Group.class));
        view.addAvailableGroupParticipants(groups);
        verify(taskDetail).addGroupParticipants(groups);
    }

    @Test
    public void testGetTasks() {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(mock(Task.class));
        when(tasksContainer.getRowModels(0)).thenReturn(new ArrayList<Task>());
        when(tasksContainer.getRowModels(1)).thenReturn(tasks);

        assertEquals(0, view.getTasks(0).size());
        verify(tasksContainer).getRowModels(0);

        assertEquals(1, view.getTasks(1).size());
        verify(tasksContainer).getRowModels(1);
    }

    @Test
    public void testGetWidgets() {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(mock(Widget.class));
        when(tasksContainer.getRowWidgets(0)).thenReturn(new ArrayList<Widget>());
        when(tasksContainer.getRowWidgets(1)).thenReturn(widgets);

        assertEquals(0, view.getWidgets(0).size());
        verify(tasksContainer).getRowWidgets(0);

        assertEquals(1, view.getWidgets(1).size());
        verify(tasksContainer).getRowWidgets(1);
    }

    @Test
    public void testGetRowsCount() {
        when(tasksContainer.getRowCount()).thenReturn(123);
        assertEquals(123, view.getRowsCount());
    }

    @Test
    public void testAddButtonHandler() {
        List<Widget> widgets = mock(List.class);
        when(tasksContainer.getNewRowWidgets()).thenReturn(widgets);
        view.addButtonHandler(null);
        verify(tasksContainer).addNewRow(widgets);
    }

    @Test
    public void testParallelButtonHandler() {
        view.parallelButtonHandler(null);
        verify(presenter).mergeTasks(false);
    }

    @Test
    public void testConditionButtonHandler() {
        view.conditionButtonHandler(null);
        verify(presenter).mergeTasks(true);
    }

    @Test
    public void testSplitButtonHandler() {
        view.splitButtonHandler(null);
        verify(presenter).splitTasks();
    }

    @Test
    public void testMergeSelectedWidgetsWrongCount() {
        view.mergeSelectedWidgets(false);
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.mergeSelectedWidgets(false);
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.mergeSelectedWidgets(false);
        verify(tasksContainer, never()).addWidgetToEnd(anyInt(), any(Widget.class));
        verify(tasksContainer, never()).removeRow(anyInt());
    }

    @Test
    public void testMergeSelectedSameRow() {
        ListTaskDetail one = mock(ListTaskDetail.class);
        ListTaskDetail two = mock(ListTaskDetail.class);
        view.lastSelectedWidgets.add(one);
        view.lastSelectedWidgets.add(two);
        when(tasksContainer.getRowOfWidget(one)).thenReturn(1);
        when(tasksContainer.getRowOfWidget(two)).thenReturn(1);
        view.mergeSelectedWidgets(false);
        verify(tasksContainer, never()).addWidgetToEnd(anyInt(), any(Widget.class));
        verify(tasksContainer, never()).removeRow(anyInt());
    }

    @Test
    public void testMergeSelected() {
        ListTaskDetail one = mock(ListTaskDetail.class);
        ListTaskDetail two = mock(ListTaskDetail.class);
        view.lastSelectedWidgets.add(one);
        view.lastSelectedWidgets.add(two);
        when(tasksContainer.getRowOfWidget(one)).thenReturn(0);
        when(tasksContainer.getRowOfWidget(two)).thenReturn(1);

        List<Widget> firstRow = new ArrayList<Widget>();
        MergedTasksIndicator indicator = mock(MergedTasksIndicator.class);
        firstRow.add(indicator);
        firstRow.add(one);
        when(tasksContainer.getRowWidgets(0)).thenReturn(firstRow);

        view.mergeSelectedWidgets(true);
        verify(tasksContainer).addWidgetToEnd(0, two);
        verify(tasksContainer).removeRow(1);
        verify(indicator).setVisible(true);
    }

    @Test
    public void testSplitSelectedWidgetsWrongCount() {
        view.splitSelectedWidgets();
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.splitSelectedWidgets();
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.splitSelectedWidgets();
        verify(tasksContainer, never()).getRowOfWidget(any(Widget.class));
        verify(tasksContainer, never()).split(anyInt());
    }

    @Test
    public void testSplitSelectedWidgetsDifferentRows() {
        ListTaskDetail one = mock(ListTaskDetail.class);
        ListTaskDetail two = mock(ListTaskDetail.class);
        view.lastSelectedWidgets.add(one);
        view.lastSelectedWidgets.add(two);
        when(tasksContainer.getRowOfWidget(one)).thenReturn(0);
        when(tasksContainer.getRowOfWidget(two)).thenReturn(1);
        view.splitSelectedWidgets();
        verify(tasksContainer, never()).split(anyInt());
    }

    @Test
    public void testSplitSelectedWidgets() {
        ListTaskDetail one = mock(ListTaskDetail.class);
        ListTaskDetail two = mock(ListTaskDetail.class);
        view.lastSelectedWidgets.add(one);
        view.lastSelectedWidgets.add(two);
        when(tasksContainer.getRowOfWidget(one)).thenReturn(1);
        when(tasksContainer.getRowOfWidget(two)).thenReturn(1);
        view.splitSelectedWidgets();
        verify(tasksContainer).split(1);
    }

    @Test
    public void testDeselectAll() {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(mock(Widget.class));
        view.lastSelectedWidgets = widgets;
        view.deselectAll();
        verify(tasksContainer).highlightWidgets(new ArrayList<Widget>());
    }

    @Test
    public void testHighlightSelectedEmpty() {
        List<Widget> widgets = new ArrayList<Widget>();
        view.lastSelectedWidgets = widgets;

        view.highlightSelected();
        verify(tasksContainer).highlightWidgets(widgets);
    }

    @Test
    public void testHighlightSelected() {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(mock(Widget.class));
        widgets.add(mock(Widget.class));
        view.lastSelectedWidgets = widgets;
        view.highlightSelected();
        verify(tasksContainer).highlightWidgets(widgets);
    }

    @Test
    public void testShowHumanSpecificDetails() {
        view.showHumanSpecificDetails();
        verify(taskDetail).showHumanDetails();
    }

    @Test
    public void testShowServiceSpecificDetails() {
        view.showServiceSpecificDetails();
        verify(taskDetail).showServiceDetails();
    }

    @Test
    public void testUnbindAllTaskWidgets() {
        List<Widget> firstRow = new ArrayList<Widget>();
        ListTaskDetail one = mock(ListTaskDetail.class);
        firstRow.add(one);
        List<Widget> secondRow = new ArrayList<Widget>();
        ListTaskDetail two = mock(ListTaskDetail.class);
        secondRow.add(two);
        when(tasksContainer.getRowCount()).thenReturn(2);
        when(tasksContainer.getRowWidgets(0)).thenReturn(firstRow);
        when(tasksContainer.getRowWidgets(1)).thenReturn(secondRow);
        view.unbindAllTaskWidgets();
        verify(one).unbind();
        verify(two).unbind();
        verify(taskDetail).unbind();
    }

    @Test
    public void rebindTaskDetailWidgets() {
        view.rebindTaskDetailWidgets();
        verify(taskDetail).rebind();
    }

    @Test
    public void testSetModelTaskDetailWidgets() {
        Task model = mock(Task.class);
        view.setModelTaskDetailWidgets(model);
        verify(taskDetail).setModel(model);
        verify(taskIO).setModel(model);
    }

    @Test
    public void testRebindConditionWidgetToModel() {
        Condition model = mock(Condition.class);
        view.rebindConditionWidgetToModel(model);
        verify(conditionWidget).unbind();
        verify(conditionWidget).setModel(model);
        verify(conditionWidget).rebind();
    }

    @Test
    public void testShowSplitInvalidCount() {
        view.showSplitInvalidCount();
        verify(notification).fire(notificationEvent.capture());
        assertEquals(notificationEvent.getValue().getNotification(), DesignerEditorConstants.INSTANCE.splitInvalidRowCount());
    }

    @Test
    public void testShowMergeInvalidCount() {
        view.showMergeInvalidCount();
        verify(notification).fire(notificationEvent.capture());
        assertEquals(notificationEvent.getValue().getNotification(), DesignerEditorConstants.INSTANCE.mergeInvalidTaskCount());
    }

    @Test
    public void showAlreadyContainsMerged() {
        view.showAlreadyContainsMerged();
        verify(notification).fire(notificationEvent.capture());
        assertEquals(notificationEvent.getValue().getNotification(), DesignerEditorConstants.INSTANCE.containsAlreadyMerged());
    }

    @Test
    public void testSetNameHelpVisibility() {
        view.setNameHelpVisibility(false);
        verify(taskDetail, never()).setNameHelpVisibility(true);
        verify(taskDetail).setNameHelpVisibility(false);

        view.setNameHelpVisibility(true);
        verify(taskDetail).setNameHelpVisibility(true);
        verify(taskDetail).setNameHelpVisibility(false);
    }

    @Test
    public void testSetParticipantHelpVisibility() {
        view.setParticipantHelpVisibility(false);
        verify(taskDetail, never()).setParticipantHelpVisibility(true);
        verify(taskDetail).setParticipantHelpVisibility(false);

        view.setParticipantHelpVisibility(true);
        verify(taskDetail).setParticipantHelpVisibility(true);
        verify(taskDetail).setParticipantHelpVisibility(false);
    }

    @Test
    public void testSetOperationHelpVisibility() {
        view.setOperationHelpVisibility(false);
        verify(taskDetail, never()).setOperationHelpVisibility(true);
        verify(taskDetail).setOperationHelpVisibility(false);

        view.setOperationHelpVisibility(true);
        verify(taskDetail).setOperationHelpVisibility(true);
        verify(taskDetail).setOperationHelpVisibility(false);
    }

    @Test
    public void testSetVariableHelpVisibility() {
        view.setVariableHelpVisibility(false);
        verify(conditionWidget, never()).setVariableHelpVisibility(true);
        verify(conditionWidget).setVariableHelpVisibility(false);

        view.setVariableHelpVisibility(true);
        verify(conditionWidget).setVariableHelpVisibility(true);
        verify(conditionWidget).setVariableHelpVisibility(false);
    }

    @Test
    public void testSetConstraintValueHelpVisibility() {
        view.setConstraintValueHelpVisibility(false);
        verify(conditionWidget, never()).setConstraintValueHelpVisibility(true);
        verify(conditionWidget).setConstraintValueHelpVisibility(false);

        view.setConstraintValueHelpVisibility(true);
        verify(conditionWidget).setConstraintValueHelpVisibility(true);
        verify(conditionWidget).setConstraintValueHelpVisibility(false);
    }

    @Test
    public void testSetConstraintHelpVisibility() {
        view.setConstraintHelpVisibility(false);
        verify(conditionWidget, never()).setConstraintHelpVisibility(true);
        verify(conditionWidget).setConstraintHelpVisibility(false);

        view.setConstraintHelpVisibility(true);
        verify(conditionWidget).setConstraintHelpVisibility(true);
        verify(conditionWidget).setConstraintHelpVisibility(false);
    }

    @Test
    public void testSetMergeButtonsVisibility() {
        view.setMergeButtonsVisibility(true);
        verify(parallelButton).setVisible(true);
        verify(conditionButton).setVisible(true);
        verify(parallelButton, never()).setVisible(false);
        verify(conditionButton, never()).setVisible(false);

        view.setMergeButtonsVisibility(false);
        verify(parallelButton).setVisible(true);
        verify(conditionButton).setVisible(true);
        verify(parallelButton).setVisible(false);
        verify(conditionButton).setVisible(false);
    }

    @Test
    public void testSetSplitButtonVisibility() {
        view.setSplitButtonVisibility(true);
        verify(splitButton).setVisible(true);
        verify(splitButton, never()).setVisible(false);

        view.setSplitButtonVisibility(false);
        verify(splitButton).setVisible(true);
        verify(splitButton).setVisible(false);
    }

    @Test
    public void testSetConditionPanelVisibility() {
        view.setConditionPanelVisibility(true);
        verify(conditionPanel).setVisible(true);
        verify(conditionPanel, never()).setVisible(false);
        view.setConditionPanelVisibility(false);
        verify(conditionPanel).setVisible(true);
        verify(conditionPanel).setVisible(false);

    }

    @Test
    public void testAddAvailableOperation() {
        Operation operation = mock(Operation.class);
        List<Operation> operations = new ArrayList<Operation>();
        operations.add(operation);
        view.setAcceptableOperations(operations);
        verify(taskDetail).setAcceptableOperations(operations);
    }

    @Test
    public void testSetAvailableDataTypes() {
        List<String> dataTypes = new ArrayList<String>();
        dataTypes.add("dataType");
        view.setAvailableDataTypes(dataTypes);
        verify(taskIO).setAvailableDataTypes(dataTypes);
    }

    @Test
    public void testSetTaskPanelVisibility() {
        view.setTaskPanelVisibility(true);
        verify(taskDetailPanel).setVisible(true);
        verify(taskDetailPanel, never()).setVisible(false);
        view.setTaskPanelVisibility(false);
        verify(taskDetailPanel).setVisible(true);
        verify(taskDetailPanel).setVisible(false);
    }
}
