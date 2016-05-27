package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.wizard.util.CompareUtils;
import org.jbpm.designer.client.wizard.util.DefaultValues;
import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.Operation;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

public class TaskIO extends Composite implements HasModel<Task> {

    interface TaskIOBinder extends UiBinder<Widget, TaskIO> {
    }

    private static TaskIOBinder uiBinder = GWT.create(TaskIOBinder.class);

    DataBinder<Task> dataBinder;

    @Inject
    Event<NotificationEvent> notification;

    @UiField
    FieldSet inputsFieldSet;

    @UiField
    FieldSet outputsFieldSet;

    @Inject
    protected TaskInputsTable taskInputsTable;

    @Inject
    protected TaskOutputsTable taskOutputsTable;

    protected List<String> dataTypes;

    private DefaultValues defaultValues = new DefaultValues();

    public TaskIO() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    public void initDataBinder() {
        dataBinder = DataBinder.forType(Task.class);
    }

    @PostConstruct
    public void initializeView() {
        dataTypes = new ArrayList<String>();
        outputsFieldSet.add(taskOutputsTable);
        inputsFieldSet.add(taskInputsTable);
        taskOutputsTable.setParentWidget(this);
        taskOutputsTable.getListWidget().addValueChangeHandler(new ValueChangeHandler<List<Variable>>() {
            @Override
            public void onValueChange(ValueChangeEvent<List<Variable>> valueChangeEvent) {
                Task model = getModel();
                model.setOutputs(valueChangeEvent.getValue());
                setModel(model);
            }
        });
        taskInputsTable.getListWidget().addValueChangeHandler(new ValueChangeHandler<List<TaskInputEntry>>() {
            @Override
            public void onValueChange(ValueChangeEvent<List<TaskInputEntry>> valueChangeEvent) {
                List<Variable> taskInputs = new ArrayList<Variable>();
                for(int i = 0; i < taskInputsTable.getListWidget().getWidgetCount(); i++) {
                    TaskInputRow widget = taskInputsTable.getListWidget().getWidget(i);
                    if(widget.getModel().isSelected()) {
                        taskInputs.add(widget.getModel().getVariable());
                    }
                }
                Task model = getModel();
                Map<String, Variable> inputs = new HashMap<String, Variable>();
                for(Variable variable : taskInputs) {
                    inputs.put(variable.getName(), variable);
                }
                model.setInputs(inputs);
                setModel(model);
            }
        });
    }

    public void setPropertyChangeChandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }

    public void setAcceptableValues(List<Variable> vars) {
        List<TaskInputEntry> acceptableValues = new ArrayList<TaskInputEntry>();
        for(Variable variable : vars) {
            TaskInputEntry entry = new TaskInputEntry();
            entry.setVariable(variable);
            entry.setSelected(false);
            acceptableValues.add(entry);
        }
        taskInputsTable.getListWidget().setValue(acceptableValues);
    }

    @Override
    public Task getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Task task) {
        dataBinder.setModel(task);
        if (task.getInputs() != null) {
            Collection<Variable> taskInputs = task.getInputs().values();
            for(int i = 0; i < taskInputsTable.getListWidget().getWidgetCount(); i++) {
                TaskInputRow widget = taskInputsTable.getListWidget().getWidget(i);
                if(taskInputs.contains(widget.getModel().getVariable())) {
                    widget.getModel().setSelected(true);
                }
            }
        }

        if (task.getOutputs() != null) {
            taskOutputsTable.getListWidget().setValue(new ArrayList<Variable>());
            for (Variable variable : task.getOutputs()) {
                taskOutputsTable.addVariable(variable, dataTypes);
            }
        }
    }

    public void setAvailableDataTypes(List<String> availableDataTypes) {
        dataTypes.clear();
        if(availableDataTypes != null) {
            dataTypes.addAll(availableDataTypes);
        }
    }

    public void addVariable() {
        if(getModel() != null) {
            if(getModel() instanceof HumanTask) {
                taskOutputsTable.addVariable(defaultValues.getDefaultVariable(), dataTypes);
            } else if(getModel() instanceof ServiceTask) {
                ServiceTask serviceTask = (ServiceTask) getModel();
                if(serviceTask.getOperation() != null) {
                    Operation operation = serviceTask.getOperation();
                    for(String dataType : dataTypes) {
                        if(CompareUtils.areSchemeAndDataTypeSame(operation.getResponseScheme(), dataType)) {
                            Variable variable = defaultValues.getDefaultVariable();
                            variable.setName("");
                            variable.setDataType(dataType);
                            taskOutputsTable.addVariable(variable, Arrays.asList(dataType));
                        }
                    }
                    if(getModel().getOutputs() == null || getModel().getOutputs().size() == 0) {
                        notification.fire(new NotificationEvent("No compatible data type for operation output", NotificationEvent.NotificationType.ERROR));
                    }
                } else {
                    notification.fire(new NotificationEvent("Select operation at first", NotificationEvent.NotificationType.WARNING));
                }
            }
        }
    }
}


