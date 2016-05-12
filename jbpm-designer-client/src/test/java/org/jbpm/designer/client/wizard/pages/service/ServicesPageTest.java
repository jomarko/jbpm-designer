package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ServicesPageTest {

    @Mock
    ServicesPageView view;

    @Mock
    FormPanel.SubmitEvent submitEvent;

    @Mock
    AbstractForm.SubmitCompleteEvent submitCompleteEvent;

    @Spy
    @InjectMocks
    ServicesPage page = new ServicesPage();

    @Test
    public void testInitialise() throws Exception {
        page.initialise();
        verify(view).init(page);
    }

    @Test
    public void testHandleSubmitNull() throws Exception {
        when(view.getFileName()).thenReturn(null);
        page.handleSubmit(submitEvent);
        verify(view).showSelectFileUploadWarning();
        verify(submitEvent).cancel();
        verify(view, never()).showUnsupportedFileTypeWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void testHandleSubmitEmpty() throws Exception {
        when(view.getFileName()).thenReturn("");
        page.handleSubmit(submitEvent);
        verify(view).showSelectFileUploadWarning();
        verify(submitEvent).cancel();
        verify(view, never()).showUnsupportedFileTypeWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void testHandleSubmitMissingExtension() throws Exception {
        when(view.getFileName()).thenReturn("abc");
        page.handleSubmit(submitEvent);
        verify(view).showUnsupportedFileTypeWarning();
        verify(submitEvent).cancel();
        verify(view, never()).showSelectFileUploadWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void testHandleSubmitWrongExtension() throws Exception {
        when(view.getFileName()).thenReturn("abc.json");
        page.handleSubmit(submitEvent);
        verify(view).showUnsupportedFileTypeWarning();
        verify(submitEvent).cancel();
        verify(view, never()).showSelectFileUploadWarning();
        verify(view, never()).showUploadingBusy();
    }

    @Test
    public void testHandleSubmit() throws Exception {
        when(view.getFileName()).thenReturn("file.swagger");
        page.handleSubmit(submitEvent);
        verify(view).showUploadingBusy();
        verify(view, never()).showUnsupportedFileTypeWarning();
        verify(submitEvent, never()).cancel();
        verify(view, never()).showSelectFileUploadWarning();
    }

    @Test
    public void testSubmitCompleteOk() throws Exception {
        String okResult = "Ok: anything";
        when(submitCompleteEvent.getResults()).thenReturn(okResult);
        page.handleSubmitComplete(submitCompleteEvent);
        verify(view).showUploadingResult(okResult);
    }

    @Test
    public void testSubmitCompleteError() throws Exception {
        String errorResult = "Error: anything";
        when(submitCompleteEvent.getResults()).thenReturn(errorResult);
        page.handleSubmitComplete(submitCompleteEvent);
        verify(view).showUploadingResult(errorResult);
    }

    @Test
    public void testSubmitCompleteUnknown() throws Exception {
        when(view.getFileName()).thenReturn("file");
        when(submitCompleteEvent.getResults()).thenReturn("unknown");
        page.handleSubmitComplete(submitCompleteEvent);
        verify(view).showUploadingResult("Error: file");
    }
}
