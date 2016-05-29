package org.jbpm.designer.client.wizard;

import org.jbpm.designer.client.wizard.pages.general.GeneralProcessInfoPage;
import org.jbpm.designer.client.wizard.pages.inputs.ProcessInputsPage;
import org.jbpm.designer.client.wizard.pages.service.ServicesPage;
import org.jbpm.designer.client.wizard.pages.start.ProcessStartEventPage;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPage;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.SwaggerDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GuidedProcessWizardTest {

    @Mock
    WizardView view;

    @Mock
    GeneralProcessInfoPage generalInfoPage;

    @Mock
    ProcessInputsPage inputsPage;

    @Mock
    ProcessStartEventPage startEventPage;

    @Mock
    ProcessTasksPage tasksPage;

    @Mock
    ServicesPage servicesPage;

    @Mock
    Callback<Boolean> callback;

    @Mock
    Callback<BusinessProcess> completeProcessCallback;

    @Spy
    @InjectMocks
    GuidedProcessWizard wizard = new GuidedProcessWizard();

    @Captor
    ArgumentCaptor<BusinessProcess> processCaptor;

    @Before
    public void setUp() throws Exception {
        wizard.setupPages();
    }

    @Test
    public void testIsComplete() throws Exception {
        wizard.isComplete(callback);

        verify(generalInfoPage).isComplete(any(Callback.class));
        verify(inputsPage).isComplete(any(Callback.class));
        verify(startEventPage).isComplete(any(Callback.class));
        verify(tasksPage).isComplete(any(Callback.class));
        verify(servicesPage).isComplete(any(Callback.class));
    }

    @Test
    public void testGetPageWidget() {
        for(int i = 0; i < 5; i++) {
            wizard.getPageWidget(i);
        }

        verify(generalInfoPage).prepareView();
        verify(inputsPage).prepareView();
        verify(startEventPage).prepareView();
        verify(tasksPage).prepareView();
        verify(servicesPage).prepareView();

        for(int i = 0; i < 5; i++) {
            wizard.getPageWidget(i);
        }

        verify(generalInfoPage, times(2)).prepareView();
        verify(inputsPage, times(2)).prepareView();
        verify(startEventPage, times(2)).prepareView();
        verify(tasksPage, times(2)).prepareView();
        verify(servicesPage, times(2)).prepareView();
    }

    @Test
    public void testCompleteProcessName() throws Exception {
        when(generalInfoPage.getProcessName()).thenReturn("xyz");
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals("xyz", processCaptor.getValue().getProcessName());
    }

    @Test
    public void testCompleteDocumentation() throws Exception {
        when(generalInfoPage.getProcessDocumentation()).thenReturn("docXyz");
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals("docXyz", processCaptor.getValue().getProcessDocumentation());
    }

    @Test
    public void testCompleteStartStandard() throws Exception {
        when(startEventPage.getStartEvent()).thenReturn(new StandardEvent());
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertTrue(processCaptor.getValue().getStartEvent() instanceof  StandardEvent);
    }

    @Test
    public void testCompleteStartSignal() throws Exception {
        SignalEvent signal = new SignalEvent();
        signal.setSignalName("abc");
        when(startEventPage.getStartEvent()).thenReturn(signal);
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals(signal, processCaptor.getValue().getStartEvent());
    }

    @Test
    public void testCompleteStartTimer() throws Exception {
        TimerEvent timer = new TimerEvent();
        timer.setTimerExpression("exp");
        timer.setTimerType("type");
        when(startEventPage.getStartEvent()).thenReturn(timer);
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals("exp", ((TimerEvent) processCaptor.getValue().getStartEvent()).getTimerExpression());
        assertEquals("type", ((TimerEvent) processCaptor.getValue().getStartEvent()).getTimerType());
    }

    @Test
    public void testCompleteDefinitions() throws Exception {
        Map<String, SwaggerDefinition> defs = new HashMap<String, SwaggerDefinition>();
        defs.put("xyz", mock(SwaggerDefinition.class));
        when(servicesPage.getDefinitions()).thenReturn(defs);
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals(defs, processCaptor.getValue().getDefinitions());
        assertTrue(processCaptor.getValue().getDefinitions().containsKey("xyz"));
    }

    @Test
    public void testCompleteTasks() throws Exception {
        Map<Integer, List<Task>> tasks = new HashMap<Integer, List<Task>>();
        tasks.put(0, Arrays.asList(mock(Task.class)));
        when(tasksPage.getTasks()).thenReturn(tasks);
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals(tasks, processCaptor.getValue().getTasks());
    }

    @Test
    public void testCompleteConditions() throws Exception {
        Map<Integer, List<Condition>> conditions = new HashMap<Integer, List<Condition>>();
        conditions.put(0, Arrays.asList(mock(Condition.class)));
        when(tasksPage.getMergedRowsWithConditions()).thenReturn(conditions);
        wizard.complete();
        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals(conditions, processCaptor.getValue().getConditions());
    }

    @Test
    public void testCompleteVariables() throws Exception {
        List<Variable> inputs = new ArrayList<Variable>();
        Variable initial = mock(Variable.class);
        inputs.add(initial);
        when(inputsPage.getInputs()).thenReturn(inputs);

        Map<Integer, List<Task>> tasks = new HashMap<Integer, List<Task>>();
        Task task  = new Task();
        Variable additional = mock(Variable.class);
        when(additional.getName()).thenReturn("additional");
        task.setOutputs(Arrays.asList(additional));
        tasks.put(0, Arrays.asList(task));
        when(tasksPage.getTasks()).thenReturn(tasks);

        wizard.complete();

        verify(completeProcessCallback).callback(processCaptor.capture());
        assertEquals(inputs, processCaptor.getValue().getInitialVariables());
        assertEquals(task.getOutputs(), processCaptor.getValue().getAdditionalVariables());
    }

    @Test
    public void testSetProcessName() throws Exception {
        wizard.setProcessName("abc");
        verify(generalInfoPage).setProcessName("abc");
    }

    @Test
    public void testGetSwaggers() {
        wizard.getSwaggers();
        verify(servicesPage).getSwaggers();
    }

    @Test
    public void testGetDefinitions() {
        wizard.getDefinitions();
        verify(servicesPage).getDefinitions();
    }

    @Test
    public void testGetInitialInputs() throws Exception {
        List<Variable> inputs = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("a");
        variable.setDataType("b");
        inputs.add(variable);

        when(inputsPage.getInputs()).thenReturn(inputs);

        List<Variable> copiedInputs = wizard.getInitialInputs();
        assertEquals(1, copiedInputs.size());
        assertTrue(copiedInputs.contains(variable));
    }
}
