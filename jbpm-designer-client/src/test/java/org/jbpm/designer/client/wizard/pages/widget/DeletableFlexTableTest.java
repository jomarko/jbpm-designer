package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DeletableFlexTableTest {

    @GwtMock
    FlexTable container;

    DeletableFlexTable table;

    @Before
    public void setUp() throws Exception {
        table =  new DeletableFlexTable() {
            @Override
            public List<Widget> getNewRowWidgets() {
                return mock(List.class);
            }
        };

        table.container = container;
    }

    @Test
    public void testClear() {
        table.clear();

        verify(container).removeAllRows();
    }
}
