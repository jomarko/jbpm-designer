package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RadioButton;
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
public class TimerWidgetTest {
    Event<NotificationEvent> notification = Mockito.mock(EventSourceMock.class);

    @Mock
    TextBox timer;

    @Mock
    ProcessStartEventPageView.Presenter presenter;

    @Captor
    ArgumentCaptor<ValueChangeHandler<String>> handlerCaptor;

    @Captor
    ArgumentCaptor<NotificationEvent> eventCaptor;

    private TimerWidget widget;

    @Before
    public void setUp() throws Exception {
        widget = new TimerWidget();
        widget.timerValue = timer;
        widget.notification = notification;
        widget.presenter = presenter;
        widget.date = Mockito.mock(RadioButton.class);
        widget.cycle = Mockito.mock(RadioButton.class);
        widget.delay = Mockito.mock(RadioButton.class);
    }

    @Test
    public void testIsValid() throws Exception {
        Mockito.when(timer.addValueChangeHandler(handlerCaptor.capture())).thenCallRealMethod();
        widget.initValueChangeHandlers();
        Mockito.when(presenter.isStartValid()).thenReturn(true);
        handlerCaptor.getValue().onValueChange(null);
        Mockito.verify(notification, Mockito.never()).fire(Matchers.any(NotificationEvent.class));
    }

    @Test
    public void testIsInvalidDelay() throws Exception {
        Mockito.when(widget.delay.getValue()).thenReturn(true);
        Mockito.when(timer.addValueChangeHandler(handlerCaptor.capture())).thenCallRealMethod();
        widget.initValueChangeHandlers();
        Mockito.when(presenter.isStartValid()).thenReturn(false);
        handlerCaptor.getValue().onValueChange(null);
        Mockito.verify(notification).fire(eventCaptor.capture());
        assertEquals(DesignerEditorConstants.INSTANCE.timerCycleAndDelayFormat(), eventCaptor.getValue().getNotification());
    }

    @Test
    public void testIsInvalidCycle() throws Exception {
        Mockito.when(widget.cycle.getValue()).thenReturn(true);
        Mockito.when(timer.addValueChangeHandler(handlerCaptor.capture())).thenCallRealMethod();
        widget.initValueChangeHandlers();
        Mockito.when(presenter.isStartValid()).thenReturn(false);
        handlerCaptor.getValue().onValueChange(null);
        Mockito.verify(notification).fire(eventCaptor.capture());
        assertEquals(DesignerEditorConstants.INSTANCE.timerCycleAndDelayFormat(), eventCaptor.getValue().getNotification());
    }

    @Test
    public void testIsInvalidDate() throws Exception {
        Mockito.when(widget.date.getValue()).thenReturn(true);
        Mockito.when(timer.addValueChangeHandler(handlerCaptor.capture())).thenCallRealMethod();
        widget.initValueChangeHandlers();
        Mockito.when(presenter.isStartValid()).thenReturn(false);
        handlerCaptor.getValue().onValueChange(null);
        Mockito.verify(notification).fire(eventCaptor.capture());
        assertEquals(DesignerEditorConstants.INSTANCE.timerDateFormat(), eventCaptor.getValue().getNotification());
    }
}
