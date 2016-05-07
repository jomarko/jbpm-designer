package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.designer.client.wizard.pages.widget.TaskDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessTasksPageViewTest {

    @Mock
    TaskDetail taskDetail;

    @Mock
    TaskIO taskIO;

    @Mock
    private ProcessTasksPageView.Presenter presenter;

    private ProcessTasksPageViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = new ProcessTasksPageViewImpl();
        view.taskDetail = taskDetail;
        view.taskIO = taskIO;
    }

    @Test
    public void testInit() throws Exception {
        view.init(presenter);

    }
}
