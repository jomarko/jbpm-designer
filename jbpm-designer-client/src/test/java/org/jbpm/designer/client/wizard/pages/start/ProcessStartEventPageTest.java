package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessStartEventPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    @Mock
    ProcessStartEventPageView view;

    private ProcessStartEventPage startPage;

    @Before
    public void init() {
        startPage = spy(ProcessStartEventPage.class);
        startPage.event = event;
        startPage.view = view;
    }

    @Test
    public void testDateFormat() {
        when(view.isSelectedDateStart()).thenReturn(true);
        when(view.getDefinedTimeValue()).thenReturn("2014-02-03T10:10");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testDateFormatText() {
        when(view.isSelectedDateStart()).thenReturn(true);
        when(view.getDefinedTimeValue()).thenReturn("lorem ipsum");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testDateFormatEmpty() {
        when(view.isSelectedDateStart()).thenReturn(true);
        when(view.getDefinedTimeValue()).thenReturn("    ");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testDateFormatMissingYear() {
        when(view.isSelectedDateStart()).thenReturn(true);
        when(view.getDefinedTimeValue()).thenReturn("02-03T10:10");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegExp() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("13d 1h 12m 2s 32ms");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testCronRegExpMissingDay() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1h 12m 2s 32ms");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testCronRegExpMissingHour() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1d 12m 2s 32ms");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testCronRegExpMissingMinute() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1d 1h 2s 32ms");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testCronRegExpMissingSecond() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("2d 1h 12m 32ms");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testCronRegExpMissingMiliSecond() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("2d 1h 12m 2s");
        assertTrue(startPage.isStartValid());
    }

    @Test
    public void testCronRegWrongOrder() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1ms 1s 12m 2h 3d");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegEmpty() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("   ");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegText() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("lorem ipsum");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegNumber() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1.0");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegSpecialChars() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1d 2# 3s 4@ 5^");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegRepeatedChars() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1d 1d");
        assertFalse(startPage.isStartValid());
    }

    @Test
    public void testCronRegOnlyNumbers() throws Exception {
        setMockFroCronTest();
        when(view.getDefinedTimeValue()).thenReturn("1 2 3 1");
        assertFalse(startPage.isStartValid());
    }

    private void setMockFroCronTest() {
        when(view.isSelectedDateStart()).thenReturn(false);
        when(view.isSelectedCycleStart()).thenReturn(true);
        when(view.isSelectedDelayStart()).thenReturn(true);
    }

}
