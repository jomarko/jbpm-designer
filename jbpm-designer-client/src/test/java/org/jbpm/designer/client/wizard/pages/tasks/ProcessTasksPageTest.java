package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.shared.Condition;
import org.jbpm.designer.client.shared.Task;
import org.jbpm.designer.client.shared.User;
import org.jbpm.designer.client.shared.Variable;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessTasksPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    @Mock
    GuidedProcessWizard wizard;

    @Mock
    ProcessTasksPageView view;

    @Mock
    ClientUserSystemManager manager;

    @Mock
    UserManager userManager;

    @Mock
    GroupManager groupManager;

    @Captor
    ArgumentCaptor<AbstractEntityManager.SearchRequest> requestCaptor;

    @Spy
    @InjectMocks
    ProcessTasksPage page = new ProcessTasksPage();

    private Variable varA;
    private Variable varB;

    private Task taskOne;
    private Task taskTwo;

    @Before
    public void setUp() {
        varA = new Variable("a", Variable.VariableType.INPUT, null, null);
        varB = new Variable("b", Variable.VariableType.OUTPUT, null, null);
        List<Variable> inputs = new ArrayList<Variable>();
        inputs.add(varA);
        inputs.add(varB);
        when(wizard.getInitialInputs()).thenReturn(inputs);

        taskOne = new Task("one");
        taskTwo = new Task("two");

        page.setWizard(wizard);

        when(manager.users(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(userManager);
        when(manager.groups(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(groupManager);
    }

    @Test
    public void testInitialise()  {
        page.initialise();
        verify(view).init(page);
    }

    @Test
    public void testPrepareView() {
        page.prepareView();


        verify(userManager).search(requestCaptor.capture());
        assertEquals("", requestCaptor.getValue().getSearchPattern());
        verify(groupManager).search(requestCaptor.capture());
        assertEquals("", requestCaptor.getValue().getSearchPattern());
    }

    @Test
    public void testIsComplete() {
        taskOne.setTaskType(Task.HUMAN_TYPE);
        taskOne.setResponsibleHuman(new User());
        taskTwo.setTaskType(Task.SERVICE_TYPE);
        taskTwo.setOperation("operation");
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);

        when(view.getTasks()).thenReturn(tasks);

        Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback).callback(true);
    }

    @Test
    public void testIsCompleteIncompleteHuman() {
        taskOne.setTaskType(Task.HUMAN_TYPE);
        taskTwo.setTaskType(Task.SERVICE_TYPE);
        taskTwo.setOperation("operation");
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);

        when(view.getTasks()).thenReturn(tasks);

        Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback).callback(false);
    }

    @Test
    public void testIsCompleteIncompleteService() {
        taskOne.setTaskType(Task.HUMAN_TYPE);
        taskOne.setResponsibleHuman(new User());
        taskTwo.setTaskType(Task.SERVICE_TYPE);
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);

        when(view.getTasks()).thenReturn(tasks);

        Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback).callback(false);
    }

    @Test
    public void testIsCompleteIncompleteCondition() {
        taskOne.setTaskType(Task.HUMAN_TYPE);
        taskOne.setResponsibleHuman(new User());
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);

        page.conditions.put(taskOne.getId(), new Condition());

        when(view.getTasks()).thenReturn(tasks);

        Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback).callback(false);
    }

    @Test
    public void testGetDefaultModel() {
        Task model = page.getDefaultModel();
        assertEquals(Task.HUMAN_TYPE, model.getTaskType());
        assertEquals("", model.getName());
    }

    @Test
    public void testGetVariablesForTaskBase() {
        Task task = mock(Task.class);
        List<Variable> result = page.getVariablesForTask(task);

        verify(wizard).getInitialInputs();

        assertTrue(result.contains(varA));
        assertTrue(result.contains(varB));

    }

    @Test
    public void testRowDeleted() {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        when(view.getTasks()).thenReturn(tasks);

        page.conditions.put(taskOne.getId(), new Condition());
        page.conditions.put(taskTwo.getId(), new Condition());

        page.rowDeleted();

        verify(view).deselectAllRows();
        assertEquals(1, page.conditions.size());
    }

    @Test
    public void testSplitTasksCondition() {
        List<Integer> selected = new ArrayList<Integer>();
        selected.add(1);
        when(view.getSelectedRows()).thenReturn(selected);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);
        when(view.getTasks(1)).thenReturn(tasks);

        when(view.getRowType(1)).thenReturn("condition");

        page.conditions.put(taskOne.getId(), new Condition());
        page.conditions.put(taskTwo.getId(), new Condition());

        page.splitTasks();

        verify(view).setRowType(1, "");
        verify(view).splitRow(1);
        assertEquals(0, page.conditions.size());
    }

    @Test
    public void testSplitTasksParallel() {
        List<Integer> selected = new ArrayList<Integer>();
        selected.add(1);
        when(view.getSelectedRows()).thenReturn(selected);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);
        when(view.getTasks(1)).thenReturn(tasks);

        when(view.getRowType(1)).thenReturn("parallel");

        page.conditions.put(taskOne.getId(), new Condition());
        page.conditions.put(taskTwo.getId(), new Condition());

        page.splitTasks();

        verify(view).setRowType(1, "");
        verify(view).splitRow(1);
        assertEquals(2, page.conditions.size());
    }

    @Test
    public void testSplitTasksNoType() {
        List<Integer> selected = new ArrayList<Integer>();
        selected.add(1);
        when(view.getSelectedRows()).thenReturn(selected);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);
        when(view.getTasks(1)).thenReturn(tasks);

        when(view.getRowType(1)).thenReturn("");

        page.conditions.put(taskOne.getId(), new Condition());
        page.conditions.put(taskTwo.getId(), new Condition());

        page.splitTasks();

        verify(view, never()).setRowType(1, "");
        verify(view, never()).splitRow(1);
        assertEquals(2, page.conditions.size());
    }

    @Test
    public void testMergeCondition() {
        List<Integer> selectedRows = new ArrayList<Integer>();
        selectedRows.add(1);
        selectedRows.add(2);
        when(view.getSelectedRows()).thenReturn(selectedRows);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);
        when(view.getTasks(anyInt())).thenReturn(tasks);

        when(view.mergeRows(anyList())).thenReturn(1);

        page.mergeTasksCondition();

        verify(view).mergeRows(selectedRows);
        verify(view).setRowType(1, "condition");
        verify(view).deselectAllRows();

        assertEquals(2, page.conditions.size());
        assertTrue(page.conditions.keySet().contains(taskOne.getId()));
        assertTrue(page.conditions.keySet().contains(taskTwo.getId()));

    }

    @Test
    public void testMergeConditionSelectedRowsCount() {
        List<Integer> selectedRows = new ArrayList<Integer>();
        when(view.getSelectedRows()).thenReturn(selectedRows);
        page.mergeTasksCondition();
        verify(view).showMergeInvalidCount();

        selectedRows.add(1);
        when(view.getSelectedRows()).thenReturn(selectedRows);
        page.mergeTasksCondition();
        verify(view, times(2)).showMergeInvalidCount();

        selectedRows.add(2);
        selectedRows.add(3);
        when(view.getSelectedRows()).thenReturn(selectedRows);
        page.mergeTasksCondition();
        verify(view, times(3)).showMergeInvalidCount();
    }

    @Test
    public void testMergeParallel() throws Exception {
        List<Integer> selectedRows = new ArrayList<Integer>();
        selectedRows.add(1);
        selectedRows.add(2);
        when(view.getSelectedRows()).thenReturn(selectedRows);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskOne);
        tasks.add(taskTwo);
        when(view.getTasks(anyInt())).thenReturn(tasks);

        when(view.mergeRows(anyList())).thenReturn(1);

        page.mergeTasksParallel();

        verify(view).setRowType(1, "parallel");
        verify(view).deselectAllRows();
    }

    @Test
    public void testMergeParallelRowCount() throws Exception {
        List<Integer> selectedRows = new ArrayList<Integer>();
        when(view.getSelectedRows()).thenReturn(selectedRows);
        page.mergeTasksParallel();

        selectedRows.add(1);
        when(view.getSelectedRows()).thenReturn(selectedRows);
        page.mergeTasksParallel();

        verify(view, never()).setRowType(anyInt(), anyString());
    }

    @Test
    public void testIsRowParallel() {
        when(view.getRowType(1)).thenReturn("");
        assertFalse(page.isRowParallel(1));

        when(view.getRowType(1)).thenReturn("parallel");
        assertTrue(page.isRowParallel(1));
    }

    @Test
    public void testIsRowCondition() {
        when(view.getRowType(1)).thenReturn("");
        assertFalse(page.isRowCondition(1));

        when(view.getRowType(1)).thenReturn("condition");
        assertTrue(page.isRowCondition(1));

    }
}
