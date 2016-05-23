package org.jbpm.designer.client.wizard.pages.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GeneralProcessInfoPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    @Mock
    GeneralProcessInfoPageView view;

    @Spy
    @InjectMocks
    private GeneralProcessInfoPage page;

    @Mock
    Callback<Boolean> callback;

    @Test
    public void testInitialise() throws Exception {
        page.initialise();
        verify(view).init(page);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testIsComplete() {
        when(view.getName()).thenReturn("abc");
        page.isComplete(callback);

        verify(view, times(1)).hideNameHelp();
        verify(view, never()).showNameHelp();
        verify(callback, times(1)).callback(true);
    }

    @Test
    public void testIsCompleteEmpty() {
        when(view.getName()).thenReturn("  ");
        page.isComplete(callback);

        verify(view, never()).hideNameHelp();
        verify(view, times(1)).showNameHelp();
        verify(callback, times(1)).callback(false);
    }
}
