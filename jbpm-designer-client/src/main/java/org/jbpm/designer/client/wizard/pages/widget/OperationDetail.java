package org.jbpm.designer.client.wizard.pages.widget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.TextArea;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.Operation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

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

    public void setVariablesForParameterMapping(List<Variable> variables) {
        parameters.setAcceptableVariables(variables);
    }

    public void rebindToModel(Operation operation) {
        dataBinder.unbind();
        dataBinder.setModel(operation);
        dataBinder.bind(description, "description")
                  .bind(parameters.getListWidget(), "parameterMappings").getModel();
    }
}
