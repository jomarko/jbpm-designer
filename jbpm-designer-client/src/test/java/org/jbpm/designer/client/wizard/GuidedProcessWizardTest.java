package org.jbpm.designer.client.wizard;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.designer.client.wizard.pages.general.GeneralProcessInfoPage;
import org.jbpm.designer.client.wizard.pages.inputs.ProcessInputsPage;
import org.jbpm.designer.client.wizard.pages.service.ServicesPage;
import org.jbpm.designer.client.wizard.pages.start.ProcessStartEventPage;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedProcessWizardTest {

    @Mock
    WizardView view;

    @Mock
    GeneralProcessInfoPage generalInfoPage;

    @Mock
    ProcessInputsPage inputsPage;

    @Mock
    ProcessStartEventPage startEventPage;

    @Mock
    ProcessTasksPage tasksPage;

    @Mock
    ServicesPage servicesPage;

    @Mock
    Callback<Boolean> callback;

    @Spy
    @InjectMocks
    GuidedProcessWizard wizard = new GuidedProcessWizard();

    @Before
    public void setUp() throws Exception {
        wizard.setupPages();
    }

    @Test
    public void testStart()  {
        wizard.start();
        verify(generalInfoPage).initialise();
        verify(inputsPage).initialise();
        verify(startEventPage).initialise();
        verify(tasksPage).initialise();
        verify(servicesPage).initialise();
    }

    @Test
    public void testIsComplete() throws Exception {
        wizard.isComplete(callback);

        verify(generalInfoPage).isComplete(any(Callback.class));
        verify(inputsPage).isComplete(any(Callback.class));
        verify(startEventPage).isComplete(any(Callback.class));
        verify(tasksPage).isComplete(any(Callback.class));
        verify(servicesPage).isComplete(any(Callback.class));
    }

    @Test
    public void testGetPageWidget() {
        for(int i = 0; i < 5; i++) {
            wizard.getPageWidget(i);
        }

        verify(generalInfoPage).prepareView();
        verify(inputsPage).prepareView();
        verify(startEventPage).prepareView();
        verify(tasksPage).prepareView();
        verify(servicesPage).prepareView();

        for(int i = 0; i < 5; i++) {
            wizard.getPageWidget(i);
        }

        verify(generalInfoPage, times(2)).prepareView();
        verify(inputsPage, times(2)).prepareView();
        verify(startEventPage, times(2)).prepareView();
        verify(tasksPage, times(2)).prepareView();
        verify(servicesPage, times(2)).prepareView();
    }
}
