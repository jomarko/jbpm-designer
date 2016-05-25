package org.jbpm.designer.client.wizard.pages.widget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.TextArea;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.Operation;
import org.jbpm.designer.model.operation.ParameterMapping;
import org.jbpm.designer.model.operation.SwaggerParameter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Dependent
public class OperationDetail extends Composite implements HasModel<Operation> {

    private DataBinder<Operation> dataBinder = DataBinder.forType(Operation.class);

    interface OperationDetailBinder
            extends
            UiBinder<Widget, OperationDetail> {
    }

    private static OperationDetailBinder uiBinder = GWT.create(OperationDetailBinder.class);

    @UiField
    TextArea description;

    @UiField
    FieldSet parametersFieldSet;

    @Inject
    private ParametersDetail parameters;

    public OperationDetail() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    public void initialize() {
        parametersFieldSet.add(parameters);
    }

    @Override
    public Operation getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Operation operation) {
        dataBinder.setModel(operation);
    }

    public void setVariablesForParameterMapping(Map<SwaggerParameter,List<Variable>> variables) {
        dataBinder.unbind();
        parameters.setAcceptableVariables(variables);
        bindDataBinder();
    }

    public void rebindToModel(Operation operation) {
        dataBinder.unbind();
        dataBinder.setModel(operation);
        bindDataBinder();
    }

    public void setRequiredParametersHelpVisibility(boolean value) {
        parameters.setRequiredParametersHelpVisibility(value);
    }

    public void addPropertyChangeHandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }

    public void clear() {
        dataBinder.unbind();
        description.clear();
        parameters.parameters.setValue(new ArrayList<ParameterMapping>());
    }

    private void bindDataBinder() {
        dataBinder.bind(description, "description")
                .bind(parameters.getListWidget(), "parameterMappings").getModel();
    }
}
