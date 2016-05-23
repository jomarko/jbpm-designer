package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.user.client.ui.FormPanel;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;
import org.kie.workbench.common.screens.search.service.SearchService;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.paging.PageResponse;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServicesPageTest {

    @Mock
    ServicesPageView view;

    @Mock
    FormPanel.SubmitEvent submitEvent;

    @Mock
    AbstractForm.SubmitCompleteEvent submitCompleteEvent;

    @Mock
    SearchService searchService;

    @Spy
    @InjectMocks
    ServicesPage page = new ServicesPage();

    @Captor
    ArgumentCaptor<ServiceUploadResultEntry> resultCaptor;

    @Before
    public void setUp() throws Exception {
        when(searchService.fullTextSearch(any(SearchTermPageRequest.class))).thenReturn(mock(PageResponse.class));
        page.searchService = new CallerMock<SearchService>(searchService);
    }

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
}
