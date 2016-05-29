package org.jbpm.designer.client.wizard;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.designer.client.wizard.pages.general.GeneralProcessInfoPage;
import org.jbpm.designer.client.wizard.pages.inputs.ProcessInputsPage;
import org.jbpm.designer.client.wizard.pages.service.ServicesPage;
import org.jbpm.designer.client.wizard.pages.start.ProcessStartEventPage;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPage;
import org.jbpm.designer.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedProcessWizardGwtTest {

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

    @Mock
    Callback<BusinessProcess> completeProcessCallback;

    @Spy
    @InjectMocks
    GuidedProcessWizard wizard = new GuidedProcessWizard();

    @Captor
    ArgumentCaptor<BusinessProcess> processCaptor;

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
}
