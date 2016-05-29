package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jbpm.designer.model.SignalEvent;
import org.jbpm.designer.model.StandardEvent;
import org.jbpm.designer.model.TimerEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ProcessStartEventPageViewTest {

    @Mock
    VerticalPanel container;

    @Mock
    HelpBlock standardHelp;

    @Mock
    TimerWidget timerDetails;

    @Mock
    RadioButton timer;

    @Mock
    SignalWidget signalDetails;

    @Mock
    RadioButton signal;

    @Mock
    RadioButton standard;

    @Mock
    TextBox signalText;

    private ProcessStartEventPageViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        view = GWT.create(ProcessStartEventPageViewImpl.class);
        view.standard = standard;
        view.timerDetails = timerDetails;
        view.timer = timer;
        view.signalDetails = signalDetails;
        view.signalDetails.signal = signalText;
        view.signal = signal;
        view.standardHelp = standardHelp;
        view.container = container;
        view.standardStartEvent = mock(StandardEvent.class);
        doCallRealMethod().when(view).init(any(ProcessStartEventPageView.Presenter.class));
        doCallRealMethod().when(view).initializeView();
        doCallRealMethod().when(view).getDefinedEvent();
        doCallRealMethod().when(view).getDefinedTimeValue();
        doCallRealMethod().when(view).getDefinedSignal();
        doCallRealMethod().when(view).isSelectedDateStart();
        doCallRealMethod().when(view).isSelectedDelayStart();
        doCallRealMethod().when(view).isSelectedCycleStart();
        doCallRealMethod().when(view).isSelectedSignalStart();
        doCallRealMethod().when(view).timerClicked(any(ClickEvent.class));
        doCallRealMethod().when(view).signalClicked(any(ClickEvent.class));
        doCallRealMethod().when(view).standardClicked(any(ClickEvent.class));
    }

    @Test
    public void testInit() throws Exception {
        ProcessStartEventPageView.Presenter presenter = mock(ProcessStartEventPageView.Presenter.class);
        view.init(presenter);
        verify(timerDetails).setPresenter(presenter);
        verify(signalDetails).setPresenter(presenter);
        verify(standardHelp).setVisible(true);
        verify(timerDetails).setVisible(false);
        verify(signalDetails).setVisible(false);
        verify(standard).setValue(true);
        verify(timer).setValue(false);
        verify(signal).setValue(false);
        verify(presenter).firePageChangedEvent();
    }

    @Test
    public void testInitialiseView() throws Exception {
        view.initializeView();
        verify(container).add(signalDetails);
        verify(container).add(timerDetails);
    }

    @Test
    public void testStandardClicked() throws Exception {
        ProcessStartEventPageView.Presenter presenter = mock(ProcessStartEventPageView.Presenter.class);
        view.presenter = presenter;
        view.standardClicked(null);
        verify(standardHelp).setVisible(true);
        verify(timerDetails).setVisible(false);
        verify(signalDetails).setVisible(false);
        verify(presenter).firePageChangedEvent();
    }

    @Test
    public void testTimerClicked() throws Exception {
        ProcessStartEventPageView.Presenter presenter = mock(ProcessStartEventPageView.Presenter.class);
        view.presenter = presenter;
        view.timerClicked(null);
        verify(standardHelp).setVisible(false);
        verify(timerDetails).setVisible(true);
        verify(signalDetails).setVisible(false);
        verify(presenter).firePageChangedEvent();
    }

    @Test
    public void testSignalClicked() throws Exception {
        ProcessStartEventPageView.Presenter presenter = mock(ProcessStartEventPageView.Presenter.class);
        view.presenter = presenter;
        view.signalClicked(null);
        verify(standardHelp).setVisible(false);
        verify(timerDetails).setVisible(false);
        verify(signalDetails).setVisible(true);
        verify(presenter).firePageChangedEvent();
    }

    @Test
    public void testIsSelectedSignalStart() {
        when(signal.getValue()).thenReturn(true);
        assertTrue(view.isSelectedSignalStart());
        when(signal.getValue()).thenReturn(false);
        assertFalse(view.isSelectedSignalStart());
    }

    @Test
    public void testGetDefinedSignal() {
        when(signalDetails.getSignal()).thenReturn("signal");
        assertEquals("signal", view.getDefinedSignal());
    }

    @Test
    public void testIsSelectedDateStart() {
        when(timer.getValue()).thenReturn(false);
        assertFalse(view.isSelectedDateStart());

        when(timer.getValue()).thenReturn(true);
        when(timerDetails.isDateSelected()).thenReturn(false);
        assertFalse(view.isSelectedDateStart());

        when(timer.getValue()).thenReturn(true);
        when(timerDetails.isDateSelected()).thenReturn(true);
        assertTrue(view.isSelectedDateStart());
    }

    @Test
    public void testIsSelectedDelayStart() {
        when(timer.getValue()).thenReturn(false);
        assertFalse(view.isSelectedDelayStart());

        when(timer.getValue()).thenReturn(true);
        when(timerDetails.isDelaySelected()).thenReturn(false);
        assertFalse(view.isSelectedDelayStart());

        when(timer.getValue()).thenReturn(true);
        when(timerDetails.isDelaySelected()).thenReturn(true);
        assertTrue(view.isSelectedDelayStart());
    }

    @Test
    public void testIsSelectedCycleStart() {
        when(timer.getValue()).thenReturn(false);
        assertFalse(view.isSelectedCycleStart());

        when(timer.getValue()).thenReturn(true);
        when(timerDetails.isCycleSelected()).thenReturn(false);
        assertFalse(view.isSelectedCycleStart());

        when(timer.getValue()).thenReturn(true);
        when(timerDetails.isCycleSelected()).thenReturn(true);
        assertTrue(view.isSelectedCycleStart());
    }

    @Test
    public void testGetDefinedTimeValue() {
        when(timerDetails.getTimerValue()).thenReturn("timer");
        assertEquals("timer", view.getDefinedTimeValue());
    }

    @Test
    public void getDefinedEventStandard() {
        when(timer.getValue()).thenReturn(false);
        when(signal.getValue()).thenReturn(false);
        assertTrue(view.getDefinedEvent() instanceof StandardEvent);
        assertFalse(view.getDefinedEvent() instanceof TimerEvent);
        assertFalse(view.getDefinedEvent() instanceof SignalEvent);
    }

    @Test
    public void getDefinedEventSignal() {
        when(timer.getValue()).thenReturn(false);
        when(signal.getValue()).thenReturn(false);
        when(signalDetails.getModel()).thenReturn(new SignalEvent());
        assertFalse(view.getDefinedEvent() instanceof TimerEvent);
        assertFalse(view.getDefinedEvent() instanceof SignalEvent);

        when(signal.getValue()).thenReturn(true);
        assertFalse(view.getDefinedEvent() instanceof TimerEvent);
        assertTrue(view.getDefinedEvent() instanceof SignalEvent);
    }

    @Test
    public void getDefinedEventTimer() {
        when(timer.getValue()).thenReturn(false);
        when(signal.getValue()).thenReturn(false);
        when(timerDetails.getModel()).thenReturn(new TimerEvent());
        assertFalse(view.getDefinedEvent() instanceof TimerEvent);
        assertFalse(view.getDefinedEvent() instanceof SignalEvent);

        when(timer.getValue()).thenReturn(true);
        assertTrue(view.getDefinedEvent() instanceof TimerEvent);
        assertFalse(view.getDefinedEvent() instanceof SignalEvent);
    }
}
