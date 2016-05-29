package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.jbpm.designer.client.wizard.pages.widget.*;
import org.jbpm.designer.model.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@WithClassesToStub(ValueListBox.class)
@RunWith(GwtMockitoTestRunner.class)
public class ProcessTasksPageViewGwtTest {

    @Mock
    TaskDetail taskDetail;

    @Mock
    TaskIO taskIO;

    @Mock
    TabPane taskIoPane;

    @Mock
    TabPane taskDetailPane;

    @Mock
    TasksTable tasksContainer;

    @Mock
    ConditionWidget conditionWidget;

    @Mock
    Button conditionButton;

    @Mock
    Button parallelButton;

    @Mock
    Button splitButton;

    @Mock
    TabPanel conditionPanel;

    @Mock
    TabPanel taskDetailPanel;

    @Mock
    private ProcessTasksPageView.Presenter presenter;

    Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    ArgumentCaptor<ClickHandler> clickCaptor;

    @Captor
    ArgumentCaptor<NotificationEvent> notificationEvent;

    @Mock
    org.gwtbootstrap3.client.ui.ValueListBox<String> taskType;

    private ProcessTasksPageViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        view = new ProcessTasksPageViewImpl();
        view.taskDetail = taskDetail;
        view.taskIO = taskIO;
        view.taskIoPane = taskIoPane;
        view.taskDetailPane = taskDetailPane;
        view.tasksContainer = tasksContainer;
        view.notification = notification;
        view.conditionWidget = conditionWidget;
        view.parallelButton = parallelButton;
        view.conditionButton = conditionButton;
        view.splitButton = splitButton;
        view.conditionPanel = conditionPanel;
        view.taskDetailPanel = taskDetailPanel;
        view.taskType = taskType;

        view.init(presenter);
    }

    @Test
    public void testAddedRow() throws Exception {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(mock(MergedTasksIndicator.class));
        ListTaskDetail detail = mock(ListTaskDetail.class);
        widgets.add(detail);
        when(detail.isInitialized()).thenReturn(false);
        Task defaultModel = new Task();
        when(presenter.getDefaultModel(anyString())).thenReturn(defaultModel);
        view.addedRow(widgets);
        verify(detail).addDomHandler(clickCaptor.capture(), any(DomEvent.Type.class));
        verify(detail).setModel(defaultModel);
        verify(detail).setInitialized(true);

        ClickEvent clickEvent = mock(ClickEvent.class);
        when(clickEvent.isControlKeyDown()).thenReturn(false);
        assertEquals(0, view.lastSelectedWidgets.size());
        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(1, view.lastSelectedWidgets.size());

        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(1, view.lastSelectedWidgets.size());

        when(clickEvent.isControlKeyDown()).thenReturn(true);
        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(0, view.lastSelectedWidgets.size());

        clickCaptor.getValue().onClick(clickEvent);
        assertEquals(1, view.lastSelectedWidgets.size());
    }
}
