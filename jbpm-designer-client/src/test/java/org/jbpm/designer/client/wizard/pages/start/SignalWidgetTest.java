package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;
import javax.enterprise.event.Event;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class SignalWidgetTest {

    Event<NotificationEvent> notification = Mockito.mock(EventSourceMock.class);

    @Mock
    TextBox signal;

    @Mock
    ProcessStartEventPageView.Presenter presenter;

    @Captor
    ArgumentCaptor<ValueChangeHandler<String>> captor;

    @Captor
    ArgumentCaptor<NotificationEvent> eventCaptor;

    private SignalWidget widget;

    @Before
    public void setUp() throws Exception {
        widget = new SignalWidget();
        widget.signal = signal;
        widget.notification = notification;
        widget.presenter = presenter;
    }

    @Test
    public void testIsValid() throws Exception {
        Mockito.when(signal.addValueChangeHandler(captor.capture())).thenCallRealMethod();
        widget.initChangeHandler();
        Mockito.when(presenter.isStartValid()).thenReturn(true);
        captor.getValue().onValueChange(null);
        Mockito.verify(notification, Mockito.never()).fire(Matchers.any(NotificationEvent.class));
    }

    @Test
    public void testIsInvalid() throws Exception {
        Mockito.when(signal.addValueChangeHandler(captor.capture())).thenCallRealMethod();
        widget.initChangeHandler();
        Mockito.when(presenter.isStartValid()).thenReturn(false);
        captor.getValue().onValueChange(null);
        Mockito.verify(notification).fire(eventCaptor.capture());
        assertEquals(DesignerEditorConstants.INSTANCE.signalFormat(), eventCaptor.getValue().getNotification());
    }
}
