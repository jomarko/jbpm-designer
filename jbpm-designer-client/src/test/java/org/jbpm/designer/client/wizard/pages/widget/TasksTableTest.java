package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockito;
import org.jbpm.designer.model.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TasksTableTest {

    @Mock
    FlexTable container;

    private TasksTable table;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        table = GWT.create(TasksTable.class);
        table.container = container;
        doCallRealMethod().when(table).removeRow(anyInt());
        doCallRealMethod().when(table).addWidgetToEnd(anyInt(), any(Widget.class));
        doCallRealMethod().when(table).getRowOfWidget(any(Widget.class));
        doCallRealMethod().when(table).getNewRowWidgets();
        doCallRealMethod().when(table).getModels();
        doCallRealMethod().when(table).getRowModels(anyInt());
        doCallRealMethod().when(table).getRowCount();
        doCallRealMethod().when(table).getRowWidgets(anyInt());
    }

    @Test
    public void testRemoveRow() throws Exception {
        table.removeRow(0);
        verify(container).removeRow(0);

        table.removeRow(1);
        verify(container).removeRow(1);
    }

    @Test
    public void testAddWidgetToEnd() throws Exception {
        when(container.getCellCount(0)).thenReturn(2, 3);
        Widget widget = mock(Widget.class);
        table.addWidgetToEnd(0, widget);
        verify(container).insertCell(0, 1);
        verify(container).setWidget(0, 1, widget);
    }

    @Test
    public void testGetRowOfWidget() throws Exception {
        Widget widget = mock(Widget.class);
        when(container.getRowCount()).thenReturn(2);
        when(container.getCellCount(anyInt())).thenReturn(1);
        when(container.getWidget(1,0)).thenReturn(widget);

        assertEquals(1, table.getRowOfWidget(widget));
    }

    @Test
    public void testGetRowOfWidgetNonExisting() throws Exception {
        Widget widget = mock(Widget.class);
        when(container.getRowCount()).thenReturn(2);
        when(container.getCellCount(anyInt())).thenReturn(1);
        when(container.getWidget(1,0)).thenReturn(widget);

        assertEquals(-1, table.getRowOfWidget(mock(Widget.class)));
    }

    @Test
    public void testGetRowModels() throws Exception {
        ListTaskDetail detail = mock(ListTaskDetail.class);
        Task model = mock(Task.class);
        when(detail.getModel()).thenReturn(model);

        when(container.getRowCount()).thenReturn(2);
        when(container.getCellCount(0)).thenReturn(2);
        when(container.getCellCount(1)).thenReturn(2);
        when(container.getWidget(0,0)).thenReturn(mock(MergedTasksIndicator.class));
        when(container.getWidget(1,0)).thenReturn(detail);

        assertEquals(model, table.getModels().get(0));
        assertEquals(1, table.getModels().size());
    }

    @Test
    public void testGetRowCount() throws Exception {
        when(container.getRowCount()).thenReturn(100);
        assertEquals(100, table.getRowCount());
    }

    @Test
    public void testGetRowWidgets() throws Exception {
        Widget widget = mock(Widget.class);
        when(container.getCellCount(0)).thenReturn(3);
        when(container.getWidget(0,0)).thenReturn(null);
        when(container.getWidget(0,1)).thenReturn(widget);
        assertEquals(1, table.getRowWidgets(0).size());
        assertTrue(table.getRowWidgets(0).contains(widget));
    }

}
