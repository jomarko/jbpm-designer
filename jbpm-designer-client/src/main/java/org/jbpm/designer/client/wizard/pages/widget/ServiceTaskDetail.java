package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.*;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.jbpm.designer.client.wizard.util.DefaultValues;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.Operation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class ServiceTaskDetail extends Composite implements HasModel<ServiceTask> {

    private DataBinder<ServiceTask> dataBinder = DataBinder.forType(ServiceTask.class);

    interface ServiceTaskDetailBinder
            extends
            UiBinder<Widget, ServiceTaskDetail> {
    }

    private static ServiceTaskDetailBinder uiBinder = GWT.create(ServiceTaskDetailBinder.class);

    private List<Operation> acceptableOperations;
    private List<Variable> acceptableVariables;

    @UiField
    DataIOEditorNameTextBox name;

    @UiField
    HelpBlock nameHelp;

    @UiField
    FieldSet operationWrapper;

    @UiField
    HelpBlock operationHelp;

    @UiField(provided = true)
    ValueListBox<Operation> operation = new ValueListBox<Operation>(new Renderer<Operation>() {

        @Override
        public String render(Operation operation) {
            if(operation != null && operation.getOperationId() != null) {
                return operation.getOperationId();
            } else {
                return "";
            }
        }

        @Override
        public void render(Operation operation, Appendable appendable) throws IOException {
            String s = render(operation);
            appendable.append(s);
        }
    });

    @UiField
    Form serviceTaskDetailForm;

    @UiField
    CheckBox terminate;

    @Inject
    private OperationDetail operationDetail;

    private DefaultValues defaultValues = new DefaultValues();

    public ServiceTaskDetail() {
        initWidget(uiBinder.createAndBindUi(this));
        bindDataBinder();

        if(acceptableOperations == null) {
            acceptableOperations = new ArrayList<Operation>();
        }
        if(acceptableVariables == null) {
            acceptableVariables = new ArrayList<Variable>();
        }

        operation.addValueChangeHandler(new ValueChangeHandler<Operation>() {
            @Override
            public void onValueChange(ValueChangeEvent<Operation> valueChangeEvent) {
                operationDetail.rebindToModel(valueChangeEvent.getValue());
                operationDetail.setVariablesForParameterMapping(acceptableVariables);
            }
        });

        name.setRegExp("^[a-zA-Z0-9\\-\\.\\_\\ ]*$",
                DesignerEditorConstants.INSTANCE.Removed_invalid_characters_from_name(),
                DesignerEditorConstants.INSTANCE.Invalid_character_in_name());

        ServiceTask task = defaultValues.getDefaultServiceTask();
        setModel(task);
    }

    @PostConstruct
    public void initialize() {
        serviceTaskDetailForm.add(operationDetail);
    }

    @Override
    public ServiceTask getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(ServiceTask serviceTask) {
        dataBinder.setModel(serviceTask);
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void bindDataBinder() {
        dataBinder.bind(name, "name")
                .bind(terminate, "terminateHere")
                .bind(operation, "operation")
                .getModel();
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
        operationDetail.addPropertyChangeHandler(handler);
    }

    public void setNameHelpVisibility(boolean value) {
        nameHelp.setVisible( value );
    }

    public void setOperationHelpVisibility(boolean value) {
        operationHelp.setVisible( value );
        operationDetail.setRequiredParametersHelpVisibility( value );
    }

    public void addAvailableOperation(Operation available) {
        if(acceptableOperations == null) {
            acceptableOperations = new ArrayList<Operation>();
        }
        if(isNotSelected(available) && isNotAlreadyAcceptable(available)) {
            acceptableOperations.add(available);
        }
        operation.setAcceptableValues(acceptableOperations);
    }

    public void setVariablesForParameterMapping(List<Variable> variables) {
        if(variables != null) {
            acceptableVariables = variables;
        } else {
            acceptableVariables = new ArrayList<Variable>();
        }
        operationDetail.setVariablesForParameterMapping(acceptableVariables);
    }

    private boolean isNotAlreadyAcceptable(Operation oper) {
        List<String> operationIds = new ArrayList<String>();
        for(Operation acceptable : acceptableOperations) {
            operationIds.add(acceptable.getOperationId());
        }
        return !operationIds.contains(oper.getOperationId());
    }

    private boolean isNotSelected(Operation oper) {
        if(operation.getValue() == null) {
            return true;
        } else {
            Operation selected = operation.getValue();
            if(selected.getOperationId().compareTo(oper.getOperationId()) == 0) {
                return false;
            } else {
                return true;
            }
        }
    }
}
