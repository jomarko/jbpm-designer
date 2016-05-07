package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.inject.Inject;

@Templated("TaskInputsTable.html#widget")
public class TaskInputsTable extends Composite {

    @Inject
    @DataField
    @Table(root="tbody")
    private ListWidget<TaskInputEntry, TaskInputRow> inputs;

    public ListWidget<TaskInputEntry, TaskInputRow> getListWidget() {
        return inputs;
    }
}
