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
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;
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

    @UiField
    DataIOEditorNameTextBox name;

    @UiField
    HelpBlock nameHelp;

    @UiField
    FormLabel nameLabel;

    @UiField
    HelpBlock operationHelp;

    @UiField
    FormLabel operationLabel;

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
    CheckBox endFlow;

    @Inject
    private OperationDetail operationDetail;

    private DefaultValues defaultValues = new DefaultValues();

    private ProcessTasksPageView.Presenter presenter;

    public ServiceTaskDetail() {
        initWidget(uiBinder.createAndBindUi(this));
        bindDataBinder();

        operation.addValueChangeHandler(new ValueChangeHandler<Operation>() {
            @Override
            public void onValueChange(ValueChangeEvent<Operation> valueChangeEvent) {
                operationDetail.rebindToModel(valueChangeEvent.getValue());
                operationDetail.setVariablesForParameterMapping(
                    presenter.getAcceptableVariablesForParameter(dataBinder.getModel(), valueChangeEvent.getValue()));
                ServiceTask model = getModel();
                model.setOutputs(new ArrayList<Variable>());
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
        if(operationDetail != null) {
            if(serviceTask == null || serviceTask.getOperation() == null) {
                operationDetail.clear();
            } else {
                operationDetail.rebindToModel(serviceTask.getOperation());
                Operation operation = null;
                if(serviceTask != null) {
                    operation = serviceTask.getOperation();
                }
                operationDetail.setVariablesForParameterMapping(
                    presenter.getAcceptableVariablesForParameter(serviceTask, operation));
            }
        }
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void bindDataBinder() {
        dataBinder.bind(name, "name")
                .bind(endFlow, "endFlow")
                .bind(operation, "operation")
                .getModel();
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
        operationDetail.addPropertyChangeHandler(handler);
    }

    public void setNameHelpVisibility(boolean value) {
        nameHelp.setVisible( value );
        nameLabel.setShowRequiredIndicator(value);
    }

    public void setOperationHelpVisibility(boolean value) {
        operationHelp.setVisible( value );
        operationLabel.setShowRequiredIndicator(value);
    }

    public void setOperationParametersHelpVisibility(boolean value) {
        operationDetail.setRequiredParametersHelpVisibility( value );
    }

    public void setAcceptableOperations(List<Operation> acceptableOperations) {
        dataBinder.unbind();
        operation.setValue(null);
        operation.setAcceptableValues(acceptableOperations);
        bindDataBinder();
    }

    public void setPresenter(ProcessTasksPageView.Presenter presenter) {
        this.presenter = presenter;
    }

}
