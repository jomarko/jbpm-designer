package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.pages.inputs.InputDeletedEvent;
import org.jbpm.designer.model.Variable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskOutputsTableTest {

    Event<InputDeletedEvent> event = mock(EventSourceMock.class);

    @Captor
    ArgumentCaptor<InputDeletedEvent> deletedInput;

    @Mock
    ListWidget<Variable, TaskOutputRow> outputs;

    @Captor
    ArgumentCaptor<NotificationEvent> eventCaptor;

    private List<Variable> variables;
    private Variable variable;
    private List<String> dataTypes;

    private TaskOutputsTable table;

    @Before
    public void setUp() throws Exception {
        dataTypes = new ArrayList<String>();
        dataTypes.add("String");

        table = new TaskOutputsTable();
        table.outputs = outputs;
        table.notification = Mockito.mock(EventSourceMock.class);
        table.dataTypes = dataTypes;
        table.inputDeletedEvent = event;
        table.addButton = mock(Button.class);

        variables =  new ArrayList<Variable>();
        variable = Mockito.mock(Variable.class);
        table.initialize();
    }

    @Test
    public void testAddVariableAmountExceeded() {
        variables.add(variable);
        when(outputs.getValue()).thenReturn(variables);
        table.addVariable(variable, new ArrayList<String>());
        verify(table.notification).fire(eventCaptor.capture());
        assertEquals(DesignerEditorConstants.INSTANCE.Only_single_entry_allowed(), eventCaptor.getValue().getNotification());
    }

    @Test
    public void testAddVariable() {
        when(outputs.getValue()).thenReturn(variables);
        TaskOutputRow row = mock(TaskOutputRow.class);
        when(outputs.getWidget(0)).thenReturn(row);
        table.addVariable(variable, new ArrayList<String>());
        verify(row).setAcceptableDataTypes(new ArrayList<String>());
        verify(row).setParentWidget(table);
        verify(table.notification, never()).fire(any(NotificationEvent.class));
        assertEquals(1, variables.size());

    }

    @Test
    public void testDeleteVariable() {
        variables.add(variable);
        when(outputs.getValue()).thenReturn(variables);
        table.deleteVariable(variable);
        assertEquals(0, variables.size());

        verify(event).fire(deletedInput.capture());
        assertEquals(variable, deletedInput.getValue().getDeletedInput());
    }

    @Test
    public void testSetAvailableDataTypes() {
        List<String> newDataTypes = new ArrayList<String>();
        newDataTypes.add("Integer");

        TaskOutputRow row = mock(TaskOutputRow.class);
        when(outputs.getWidget(0)).thenReturn(row);
        when(outputs.getWidgetCount()).thenReturn(1);
        when(row.getModel()).thenReturn(variable);

        table.setAvailableDataTypes(newDataTypes);
        verify(row).setAcceptableDataTypes(newDataTypes);
        assertEquals(1, table.dataTypes.size());
        assertEquals("Integer", table.dataTypes.get(0));
    }
}
