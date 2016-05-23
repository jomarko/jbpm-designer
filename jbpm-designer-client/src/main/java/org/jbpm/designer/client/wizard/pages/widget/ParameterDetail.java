package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.util.CompareUtils;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.ParameterMapping;
import org.jbpm.designer.model.operation.SwaggerParameter;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Templated("ParametersDetail.html#parameterMapping")
public class ParameterDetail extends Composite implements HasModel<ParameterMapping> {
    @Inject
    @AutoBound
    protected DataBinder<ParameterMapping> parameterMapping;

    @Inject
    @Bound(property = "parameter.name")
    @DataField
    protected TextBox name;

    @Inject
    @Bound(property = "parameter.description")
    @DataField
    protected TextBox description;

    @Bound
    @DataField
    protected ValueListBox<Variable> variable = new ValueListBox(new ToStringRenderer());

    @DataField
    Element requiredIndicator = Document.get().createElement("sup");

    @Inject
    Event<NotificationEvent> notification;

    @Override
    public ParameterMapping getModel() {
        return parameterMapping.getModel();
    }

    @Override
    public void setModel(ParameterMapping parameterMapping) {
        this.parameterMapping.setModel(parameterMapping);
    }

    public void setAcceptableVariables(List<Variable> variables) {
        List<Variable> acceptable = new ArrayList<Variable>(variables);
        if(parameterMapping.getModel() != null && parameterMapping.getModel().getParameter() != null) {
            SwaggerParameter parameter = parameterMapping.getModel().getParameter();
            if(parameter.getSchema() != null) {

                for(Variable variable : variables) {
                    String variableDataType = variable.getDataType();
                    if(!CompareUtils.areSchemeAndDataTypeSame(parameter.getSchema(), variableDataType)) {
                        acceptable.remove(variable);
                    }
                }
            } else if(parameter.getType() != null) {
                for(Variable variable : variables) {
                    if(variable.getDataType().compareToIgnoreCase(parameter.getType()) != 0) {
                        acceptable.remove(variable);
                    }
                }
            }
        }
        if(acceptable.size() == 0) {
            notification.fire(new NotificationEvent(
                    DesignerEditorConstants.INSTANCE.noCompatibleVariableForParameter() + " " + name.getValue(),
                    NotificationEvent.NotificationType.ERROR));
        }
        variable.setAcceptableValues(acceptable);
    }

    public void showNameAsRequired(boolean required) {
        if(required) {
            requiredIndicator.getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            requiredIndicator.getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
    }
}
