package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.model.*;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.jbpm.designer.model.operation.Swagger;
import org.jbpm.designer.service.SwaggerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessTasksPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    @Mock
    GuidedProcessWizard wizard;

    @Mock
    ProcessTasksPageView view;

    @Mock
    ClientUserSystemManager manager;

    @Mock
    UserManager userManager;

    @Mock
    GroupManager groupManager;

    @Mock
    SwaggerService swaggerService;

    @Captor
    ArgumentCaptor<AbstractEntityManager.SearchRequest> requestCaptor;

    @Spy
    @InjectMocks
    ProcessTasksPage page = new ProcessTasksPage();

    private Variable varA;
    private Variable varB;

    private HumanTask taskOne;
    private ServiceTask taskTwo;

    @Before
    public void setUp() {
        Caller<SwaggerService> swaggerServiceCaller = new CallerMock<SwaggerService>(swaggerService);
        page.swaggerDefinitionService = swaggerServiceCaller;

        varA = new Variable("a", Variable.VariableType.INPUT, null, null);
        varB = new Variable("b", Variable.VariableType.OUTPUT, null, null);
        List<Variable> inputs = new ArrayList<Variable>();
        inputs.add(varA);
        inputs.add(varB);
        when(wizard.getInitialInputs()).thenReturn(inputs);

        taskOne = new HumanTask("one");
        taskTwo = new ServiceTask("two");

        page.setWizard(wizard);

        when(manager.users(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(userManager);
        when(manager.groups(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(groupManager);
    }

    @Test
    public void testInitialise()  {
        page.initialise();
        verify(view).init(page);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }
}
