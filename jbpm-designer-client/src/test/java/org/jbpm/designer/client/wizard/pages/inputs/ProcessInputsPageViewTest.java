package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInputsPageViewTest {

    @Mock
    ProcessInputsTable inputsTable;

    private ProcessInputsPageViewImpl page;

    @Before
    public void setUp() throws Exception {
        page = new ProcessInputsPageViewImpl();
        page.inputs = inputsTable;
    }

    @Test
    public void testInit() throws Exception {
        page.init(mock(ProcessInputsPageView.Presenter.class));

        verify(inputsTable).clear();
    }
}
