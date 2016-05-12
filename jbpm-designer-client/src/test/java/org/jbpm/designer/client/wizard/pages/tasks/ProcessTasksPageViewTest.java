package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TabPane;
import org.jbpm.designer.client.wizard.pages.widget.ListTaskDetail;
import org.jbpm.designer.client.wizard.pages.widget.MergedTasksIndicator;
import org.jbpm.designer.client.wizard.pages.widget.TaskDetail;
import org.jbpm.designer.client.wizard.pages.widget.TasksTable;
import org.jbpm.designer.model.Group;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
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
    private ProcessTasksPageView.Presenter presenter;

    @Captor
    ArgumentCaptor<ClickHandler> clickCaptor;

    private ProcessTasksPageViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = new ProcessTasksPageViewImpl();
        view.taskDetail = taskDetail;
        view.taskIO = taskIO;
        view.taskIoPane = taskIoPane;
        view.taskDetailPane = taskDetailPane;
        view.tasksContainer = tasksContainer;
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
    public void testAddedRow() throws Exception {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(mock(MergedTasksIndicator.class));
        ListTaskDetail detail = mock(ListTaskDetail.class);
        widgets.add(detail);
        when(detail.isInitialized()).thenReturn(false);
        Task defaultModel = new Task();
        when(presenter.getDefaultModel()).thenReturn(defaultModel);
        view.addedRow(widgets);
        verify(detail).addDomHandler(clickCaptor.capture(), any(DomEvent.Type.class));
        verify(detail).setModel(defaultModel);
        verify(detail).setInitialized(true);

        ClickEvent clickEvent = mock(ClickEvent.class);
        when(clickEvent.isControlKeyDown()).thenReturn(false);
        assertEquals(0, view.lastSelectedWidgets.size());
        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(1, view.lastSelectedWidgets.size());

        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(1, view.lastSelectedWidgets.size());

        when(clickEvent.isControlKeyDown()).thenReturn(true);
        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(0, view.lastSelectedWidgets.size());

        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(1, view.lastSelectedWidgets.size());
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
        view.mergeSelectedWidgets();
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.mergeSelectedWidgets();
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.lastSelectedWidgets.add(mock(Widget.class));
        view.mergeSelectedWidgets();
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
        view.mergeSelectedWidgets();
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

        view.mergeSelectedWidgets();
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

}
