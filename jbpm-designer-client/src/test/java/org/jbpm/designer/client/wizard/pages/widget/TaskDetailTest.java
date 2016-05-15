package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;
import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@WithClassesToStub(com.google.gwt.user.client.ui.ValueListBox.class)
@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailTest {

    @Mock
    HumanTaskDetail humanTaskDetail;

    @Mock
    ServiceTaskDetail serviceTaskDetail;

    @Mock
    ValueListBox<String> taskType;

    @Captor
    ArgumentCaptor<ServiceTask> serviceTaskCaptor;

    @Captor
    ArgumentCaptor<HumanTask> humanTaskCaptor;

    private TaskDetail taskDetail;

    @Before
    public void setUp() throws Exception {
        taskDetail = new TaskDetail();
        taskDetail.serviceTaskDetail = serviceTaskDetail;
        taskDetail.humanTaskDetail = humanTaskDetail;
        taskDetail.taskType = taskType;

    }

    @Test
    public void testInit() throws Exception {
        taskDetail.init(mock(ProcessTasksPageView.Presenter.class));
        verify(serviceTaskDetail).setModel(serviceTaskCaptor.capture());
        assertEquals("", serviceTaskCaptor.getValue().getName());
        assertEquals(new HashMap<String, Variable>(), serviceTaskCaptor.getValue().getInputs());
        assertEquals(new ArrayList<Variable>(), serviceTaskCaptor.getValue().getOutputs());
        assertNull(serviceTaskCaptor.getValue().getOperation());

        verify(humanTaskDetail).setModel(humanTaskCaptor.capture());
        assertEquals("", humanTaskCaptor.getValue().getName());
        assertEquals(new HashMap<String, Variable>(), humanTaskCaptor.getValue().getInputs());
        assertEquals(new ArrayList<Variable>(), humanTaskCaptor.getValue().getOutputs());
        assertNull(humanTaskCaptor.getValue().getResponsibleGroup());
        assertNull(humanTaskCaptor.getValue().getResponsibleHuman());
    }

    @Test
    public void testGetModelHuman() {
        when(taskType.getValue()).thenReturn(taskDetail.HUMAN_TYPE);
        HumanTask task = mock(HumanTask.class);
        when(humanTaskDetail.getModel()).thenReturn(task);
        assertEquals(task, taskDetail.getModel());
        verify(humanTaskDetail).getModel();
        verify(serviceTaskDetail, never()).getModel();
    }

    @Test
    public void testGetModelService() {
        when(taskType.getValue()).thenReturn(taskDetail.SERVICE_TYPE);
        ServiceTask task = mock(ServiceTask.class);
        when(serviceTaskDetail.getModel()).thenReturn(task);
        assertEquals(task, taskDetail.getModel());
        verify(humanTaskDetail, never()).getModel();
        verify(serviceTaskDetail).getModel();
    }

    @Test
    public void testSetModelHuman() {
        HumanTask humanTask = mock(HumanTask.class);
        taskDetail.setModel(humanTask);
        verify(humanTaskDetail).setModel(humanTask);
        verify(serviceTaskDetail, never()).setModel(any(ServiceTask.class));
    }

    @Test
    public void testSetModelService() {
        ServiceTask serviceTask = mock(ServiceTask.class);
        taskDetail.setModel(serviceTask);
        verify(humanTaskDetail, never()).setModel(any(HumanTask.class));
        verify(serviceTaskDetail).setModel(serviceTask);
    }

    @Test
    public void testUnbind() {
        taskDetail.unbind();
        verify(serviceTaskDetail).unbind();
        verify(humanTaskDetail).unbind();
    }

    @Test
    public void testShowHumanDetails() {
        taskDetail.showHumanDetails();
        verify(humanTaskDetail).setVisible(true);
        verify(serviceTaskDetail).setVisible(false);
        verify(taskType).setValue(taskDetail.HUMAN_TYPE, false);
    }

    public void showServiceDetails() {
        taskDetail.showServiceDetails();
        verify(humanTaskDetail).setVisible(false);
        verify(serviceTaskDetail).setVisible(true);
        verify(taskType).setValue(taskDetail.SERVICE_TYPE, false);
    }
}
