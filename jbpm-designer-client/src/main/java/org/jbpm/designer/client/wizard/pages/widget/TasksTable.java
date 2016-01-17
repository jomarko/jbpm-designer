package org.jbpm.designer.client.wizard.pages.widget;

import org.jbpm.designer.client.shared.Task;

public class TasksTable extends DeletableFlexTable<Task> {

    @Override
    public WidgetWithModel<Task> getNewRowWidget() {
        ListTaskDetail detail = new ListTaskDetail();
        detail.setModel(new Task(""));
        return detail;
    }

    public void clear(){
        container.clear();
    }
}
