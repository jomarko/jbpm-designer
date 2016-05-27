package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.pages.inputs.InputDeletedEvent;
import org.jbpm.designer.model.Variable;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Templated("TaskOutputsTable.html#widget")
public class TaskOutputsTable extends Composite {

    @Inject
    Event<NotificationEvent> notification;

    @Inject
    protected Event<InputDeletedEvent> inputDeletedEvent;

    private InputDeletedEvent inputDeleted;

    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<Variable, TaskOutputRow> outputs;

    @Inject
    @DataField
    Button addButton;

    private TaskIO parentWidget;

    public void setParentWidget(TaskIO parentWidget) {
        this.parentWidget = parentWidget;
    }

    @PostConstruct
    public void initialize() {
        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(parentWidget != null) {
                    parentWidget.addVariable();
                }
            }
        });
        inputDeleted = new InputDeletedEvent();
    }

    public ListWidget<Variable, TaskOutputRow> getListWidget() {
        return outputs;
    }

    public void addVariable(Variable variable, List<String> dataTypes) {
        if(outputs.getValue().size() == 0) {
            outputs.getValue().add(variable);
            TaskOutputRow widget = outputs.getWidget(outputs.getValue().size() - 1);
            widget.setAcceptableDataTypes(dataTypes);
            widget.setParentWidget(this);
        } else {
            notification.fire(new NotificationEvent(DesignerEditorConstants.INSTANCE.Only_single_entry_allowed(), NotificationEvent.NotificationType.WARNING));
        }
    }

    public void deleteVariable(Variable variable) {
        outputs.getValue().remove(variable);
        inputDeleted.setDeletedInput(variable);
        inputDeletedEvent.fire(inputDeleted);
        ValueChangeEvent.fire(outputs, outputs.getValue());
    }
}
