package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMockito;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.*;

import static org.mockito.Mockito.*;

public class TaskIOTest {

    @Mock
    DataBinder<Task> dataBinder;

    @Mock
    TaskInputsTable inputsTable;

    @Mock
    ListWidget<TaskInputEntry, TaskInputRow> inputs;

    @Mock
    TaskOutputsTable outputsTable;

    @Mock
    ListWidget<Variable, TaskOutputRow> outputs;

    @Captor
    ArgumentCaptor<List<String>> listCaptor;

    @Captor
    ArgumentCaptor<List<TaskInputEntry>> inputCaptor;

    private List<String> dataTypes;

    private TaskIO taskIO;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);
        taskIO = GWT.create(TaskIO.class);
        taskIO.taskInputsTable = inputsTable;
        taskIO.taskOutputsTable = outputsTable;
        taskIO.dataBinder = dataBinder;

        dataTypes = new ArrayList<String>();
        dataTypes.add("String");
        dataTypes.add("Integer");
        dataTypes.add("org.Pet");

        taskIO.dataTypes = dataTypes;

        when(inputsTable.getListWidget()).thenReturn(inputs);
        when(outputsTable.getListWidget()).thenReturn(outputs);
        doCallRealMethod().when(taskIO).setAcceptableValues(anyList());
        doCallRealMethod().when(taskIO).setModel(any(Task.class));
    }

    @Test
    public void testAcceptableValues() throws Exception {
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = mock(Variable.class);
        variables.add(variable);

        taskIO.setAcceptableValues(variables);
        verify(inputs).setValue(inputCaptor.capture());
        Assert.assertEquals(variable, inputCaptor.getValue().get(0).getVariable());
    }

    @Test
    public void testSetModelOutputsNull() throws Exception {
        Task task = mock(Task.class);
        when(task.getOutputs()).thenReturn(null);

        taskIO.setModel(task);
        verify(outputs, never()).setValue(anyList());
        verify(outputsTable, never()).addVariable(any(Variable.class), anyList());
    }

    @Test
    public void testSetModelOutputs() throws Exception {
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = mock(Variable.class);
        variables.add(variable);
        Task task = mock(Task.class);
        when(task.getOutputs()).thenReturn(variables);

        taskIO.setModel(task);
        verify(outputs).setValue(new ArrayList<Variable>());
        verify(outputsTable).addVariable(variable, dataTypes);
    }

    @Test
    public void testSetModelInputs() throws Exception {
        Variable variable = mock(Variable.class);
        Task task = mock(Task.class);
        Map<String, Variable> namedInputs = new HashMap<String, Variable>();
        namedInputs.put("input", variable);
        when(task.getInputs()).thenReturn(namedInputs);

        when(inputs.getWidgetCount()).thenReturn(1);
        TaskInputRow row = mock(TaskInputRow.class);
        when(inputs.getWidget(0)).thenReturn(row);
        TaskInputEntry entry = mock(TaskInputEntry.class);
        when(row.getModel()).thenReturn(entry);
        when(entry.getVariable()).thenReturn(variable);

        taskIO.setModel(task);

        verify(entry).setSelected(true);
    }
}
