package org.jbpm.designer.client.wizard.pages.widget;

import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;

import java.util.List;

public class ParallelHolder extends TasksHolder {

    public ParallelHolder(ProcessTasksPageView.Presenter presenter, List<ListTaskDetail> tasks) {
        super(presenter, tasks);
    }
}
