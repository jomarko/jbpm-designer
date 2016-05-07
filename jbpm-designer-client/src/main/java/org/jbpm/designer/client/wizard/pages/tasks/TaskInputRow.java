package org.jbpm.designer.client.wizard.pages.tasks;


import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.jbpm.designer.client.wizard.pages.widget.ToStringRenderer;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@Templated("TaskInputsTable.html#input")
public class TaskInputRow extends Composite implements HasModel<TaskInputEntry> {

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @AutoBound
    protected DataBinder<TaskInputEntry> binder;

    @Inject
    @Bound(property = "variable.name")
    @DataField
    protected DataIOEditorNameTextBox name;

    @Bound(property = "variable.dataType")
    @DataField
    protected ValueListBox<String> dataType = new ValueListBox(new ToStringRenderer());

    @Inject
    @Bound(property = "selected")
    @DataField
    CheckBox select;

    @Override
    public TaskInputEntry getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(TaskInputEntry taskInputEntry) {
        binder.setModel(taskInputEntry);
    }
}
