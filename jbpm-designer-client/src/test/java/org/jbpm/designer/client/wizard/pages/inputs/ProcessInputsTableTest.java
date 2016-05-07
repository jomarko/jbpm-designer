package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jbpm.designer.model.Variable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInputsTableTest {

    @Mock
    ProcessInputRow inputRow;

    @Mock
    ListWidget<Variable, ProcessInputRow> listWidget;

    private ProcessInputsTable table;

    @Before
    public void setUp() throws Exception {
        table = new ProcessInputsTable();
        table.inputs = listWidget;
    }

    @Test
    public void testAddVariableEmpty() throws Exception {
        when(listWidget.getValue()).thenReturn(new ArrayList<Variable>());
        when(listWidget.getWidget(0)).thenReturn(inputRow);

        assertEquals(0, table.getListWidget().getValue().size());

        Variable variable = mock(Variable.class);
        List<String> dataTypes = mock(List.class);
        table.addVariable(variable,dataTypes);

        verify(inputRow).setAcceptableDataTypes(dataTypes);
        verify(inputRow).setParentWidget(table);
        assertEquals(variable, table.getListWidget().getValue().get(0));
    }

    @Test
    public void testDeleteVariable() throws Exception {
        Variable variable = mock(Variable.class);
        List<Variable> variables = new ArrayList<Variable>();
        variables.add(variable);
        when(listWidget.getValue()).thenReturn(variables);

        assertEquals(1, table.getListWidget().getValue().size());
        table.deleteVariable(variable);
        assertEquals(0, table.getListWidget().getValue().size());
    }
}
