package org.jbpm.designer.client.wizard.pages.tasks;


import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.jbpm.designer.client.wizard.pages.widget.ToStringRenderer;
import org.jbpm.designer.model.Variable;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Templated("TaskOutputsTable.html#output")
public class TaskOutputRow extends Composite implements HasModel<Variable> {

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @AutoBound
    protected DataBinder<Variable> binder;

    @Inject
    @Bound
    @DataField
    protected DataIOEditorNameTextBox name;

    @Bound
    @DataField
    protected ValueListBox<String> dataType = new ValueListBox(new ToStringRenderer());

    @DataField
    Element asterisk = Document.get().createElement("sup");

    @Inject
    @DataField
    Button deleteButton;

    private TaskOutputsTable parentWidget;

    @PostConstruct
    public void initialize() {
        name.setRegExp("^[a-zA-Z]([a-zA-Z0-9\\-\\.\\_])*$",
                DesignerEditorConstants.INSTANCE.Removed_invalid_characters_from_name(),
                DesignerEditorConstants.INSTANCE.Invalid_character_in_name());
        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                parentWidget.deleteVariable(getModel());
            }
        });
        deleteButton.setIcon(IconType.TRASH);
    }

    @Override
    public Variable getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(Variable variable) {
        binder.setModel(variable);
    }

    public void setParentWidget(TaskOutputsTable parentWidget) {
        this.parentWidget = parentWidget;
    }

    public void setAcceptableDataTypes(List<String> dataTypes) {
        dataType.setAcceptableValues(dataTypes);
    }
}
