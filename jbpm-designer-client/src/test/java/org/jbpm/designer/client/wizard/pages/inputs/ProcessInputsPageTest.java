package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.jbpm.designer.model.Variable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInputsPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    @Mock
    ProcessInputsPageView view;

    @Spy
    @InjectMocks
    private ProcessInputsPage page;

    @Mock
    Callback<Boolean> callback;

    @Test
    public void testIsCompleteEmpty() {
        when(view.getInputs()).thenReturn(new ArrayList<Variable>());

        page.isComplete(callback);

        verify(view).setVariablesHelpVisibility(false);
        verify(callback).callback(true);
    }
}
