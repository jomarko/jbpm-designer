package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.jbpm.designer.model.Variable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInputsPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Mock
    ProcessInputsPageView view;

    @Spy
    @InjectMocks
    private ProcessInputsPage page;

    @Mock
    Callback<Boolean> callback;

    private List<Variable> inputs;

    @Before
    public void setUp() throws Exception {
        inputs = new ArrayList<Variable>();
    }

    @Test
    public void testInitialise() throws Exception {
        page.initialise();
        verify(view).init(page);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testIsCompleteEmpty() {
        when(view.getInputs()).thenReturn(inputs);

        page.isComplete(callback);

        verify(view).setVariablesHelpVisibility(false);
        verify(callback).callback(true);
    }

    @Test
    public void testIsCompleteValid() {
        Variable variable = mock(Variable.class);
        when(variable.getName()).thenReturn("a");
        when(variable.getDataType()).thenReturn("String");
        inputs.add(variable);
        when(view.getInputs()).thenReturn(inputs);

        page.isComplete(callback);

        verify(view).showAsValid(variable);
        verify(view).setVariablesHelpVisibility(false);
        verify(callback).callback(true);
    }

    @Test
    public void testIsCompleteEmptyName() {
        Variable variable = mock(Variable.class);
        when(variable.getName()).thenReturn("  ");
        when(variable.getDataType()).thenReturn("String");
        inputs.add(variable);
        when(view.getInputs()).thenReturn(inputs);

        page.isComplete(callback);

        verify(view).showAsInvalid(variable);
        verify(view).setVariablesHelpVisibility(true);
        verify(callback).callback(false);
    }

    @Test
    public void testIsCompleteEmptyDataType() {
        Variable variable = mock(Variable.class);
        when(variable.getName()).thenReturn("a");
        when(variable.getDataType()).thenReturn(" ");
        inputs.add(variable);
        when(view.getInputs()).thenReturn(inputs);

        page.isComplete(callback);

        verify(view).showAsInvalid(variable);
        verify(view).setVariablesHelpVisibility(true);
        verify(callback).callback(false);
    }

    @Test
    public void testIsCompleteDuplicate() {
        Variable variable = mock(Variable.class);
        when(variable.getName()).thenReturn("a");
        when(variable.getDataType()).thenReturn("String");
        Variable duplicate = mock(Variable.class);
        when(duplicate.getName()).thenReturn("a");
        when(duplicate.getDataType()).thenReturn("String");
        inputs.add(variable);
        inputs.add(duplicate);
        when(view.getInputs()).thenReturn(inputs);

        page.isComplete(callback);

        verify(view).deleteVariable(variable);
        verify(view).setVariablesHelpVisibility(true);
        verify(callback).callback(false);
    }
}
