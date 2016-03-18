package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Constraint;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;

import java.util.List;


public class ConstraintHolder extends TasksHolder implements HasModel<Constraint> {

    private DataBinder<Constraint> dataBinder = DataBinder.forType(Constraint.class);

    public ConstraintHolder(final ProcessTasksPageView.Presenter presenter, List<ListTaskDetail> tasks) {
        super(presenter, tasks);
        if(tasks != null) {
            for(ListTaskDetail detail : tasks) {
                registrations.add(detail.addHandler(new ValueChangeHandler<Task> () {
                    @Override
                    public void onValueChange(ValueChangeEvent<Task> valueChangeEvent) {
                        dataBinder.setModel(valueChangeEvent.getValue().getCondition().getConstraint());
                    }
                }, ValueChangeEvent.getType()));
            }
        }
    }

    @Override
    public Constraint getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Constraint constraint) {
        dataBinder.setModel(constraint);
    }
}
