package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class DeletableFlexTableTest {

    @GwtMock
    FlexTable container;

    DeletableFlexTable table;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        table = GWT.create(DeletableFlexTable.class);
        table.container = container;
        doCallRealMethod().when(table).clear();
    }

    @Test
    public void testClear() {
        table.clear();
        verify(container).removeAllRows();
    }
}
