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
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.ParameterMapping;
import org.jbpm.designer.model.operation.SwaggerParameter;
import org.jbpm.designer.model.operation.SwaggerSchema;

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
            if(parameter.getSchema() != null && parameter.getSchema().get$ref() != null) {
                String ref = parameter.getSchema().get$ref();
                for(Variable var : variables) {
                    String variableDataType = var.getDataType();
                    if(variableDataType != null) {
                        String[] refParts = ref.split("/");
                        String[] dataTypesParts = variableDataType.split(".");
                    }
                }
            }
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
