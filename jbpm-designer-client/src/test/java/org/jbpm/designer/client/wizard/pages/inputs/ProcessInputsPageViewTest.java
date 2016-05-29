package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class ProcessInputsPageViewTest {

    @Mock
    ProcessInputsTable inputsTable;

    @Mock
    VerticalPanel inputsPanel;

    private ProcessInputsPageViewImpl page;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        page = GWT.create(ProcessInputsPageViewImpl.class);
        doCallRealMethod().when(page).init(any(ProcessInputsPageView.Presenter.class));
        page.inputs = inputsTable;
        page.inputsPanel = inputsPanel;
    }

    @Test
    public void testInit() throws Exception {
        page.init(mock(ProcessInputsPageView.Presenter.class));
        verify(inputsPanel).add(inputsTable);
        verify(inputsTable).clear();
    }
}
