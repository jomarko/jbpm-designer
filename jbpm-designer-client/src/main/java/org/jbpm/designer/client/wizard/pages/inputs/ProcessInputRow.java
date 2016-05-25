package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.jbpm.designer.client.wizard.pages.widget.ToStringRenderer;
import org.jbpm.designer.model.Variable;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Templated("ProcessInputsTable.html#input")
public class ProcessInputRow extends Composite implements HasModel<Variable> {

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

    @Inject
    @DataField
    protected Button deleteButton;

    @DataField
    Element asterisk = Document.get().createElement("sup");

    private ProcessInputsTable parentWidget;

    @PostConstruct
    public void initialize() {
        name.setRegExp("^[a-zA-Z]([a-zA-Z0-9\\-\\.\\_])*$",
                DesignerEditorConstants.INSTANCE.Removed_invalid_characters_from_name(),
                DesignerEditorConstants.INSTANCE.Invalid_character_in_name());
        deleteButton.setIcon( IconType.TRASH );
    }

    @Override
    public Variable getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(Variable variable) {
        binder.setModel(variable);
    }

    public void setAcceptableDataTypes(List<String> dataTypes) {
        dataType.setAcceptableValues(dataTypes);
    }

    public void showAsterisk(boolean required) {
        if(required) {
            asterisk.getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            asterisk.getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(ClickEvent e) {
        parentWidget.deleteVariable(getModel());
    }

    public void setParentWidget(ProcessInputsTable parentWidget) {
        this.parentWidget = parentWidget;
    }
}
