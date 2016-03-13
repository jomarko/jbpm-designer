package org.jbpm.designer.client.wizard.pages.widget;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;

import java.util.ArrayList;
import java.util.List;

public abstract class TasksHolder extends Composite {
    private List<ListTaskDetail> tasks;
    protected List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();
    private ProcessTasksPageView.Presenter presenter;

    public TasksHolder(ProcessTasksPageView.Presenter presenter, List<ListTaskDetail> tasks) {
        this.presenter = presenter;
        this.tasks = tasks;
        HorizontalPanel panel = new HorizontalPanel();
        if(tasks != null) {
            for(ListTaskDetail detail : tasks) {
                panel.add(detail);

                registrations.add(detail.addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        TasksHolder.this.presenter.holderSelected(TasksHolder.this);
                    }
                }, ClickEvent.getType()));
            }
        }
        initWidget(panel);
    }

    public List<ListTaskDetail> getTasks() {
        return tasks;
    }

    public void removeHandlers() {
        for(HandlerRegistration registration : registrations) {
            registration.removeHandler();
        }
        registrations.clear();
    }
}
