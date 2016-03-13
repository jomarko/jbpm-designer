package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
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

        page.rowDeleted();

        verify(view).deselectAll();
    }
}
