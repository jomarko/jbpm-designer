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
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.wizard.util.CompareUtils;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.Operation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

public class TaskIO extends Composite implements HasModel<Task> {

    interface TaskIOBinder extends UiBinder<Widget, TaskIO> {
    }

    private static TaskIOBinder uiBinder = GWT.create(TaskIOBinder.class);

    DataBinder<Task> dataBinder;

    @UiField
    FieldSet inputsFieldSet;

    @UiField
    FieldSet outputsFieldSet;

    @Inject
    protected TaskInputsTable taskInputsTable;

    @Inject
    protected TaskOutputsTable taskOutputsTable;

    protected List<String> dataTypes;

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
        dataBinder.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                restrictOutputDataTypes();
            }
        });
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
        taskOutputsTable.setAvailableDataTypes(dataTypes);
    }

    public void restrictOutputDataTypes() {
        Task model = getModel();
        if(model != null && model instanceof ServiceTask) {
            Operation operation = ((ServiceTask) model).getOperation();
            if(operation != null && operation.getResponseScheme()!= null) {
                List<String> restrictedDataTypes = new ArrayList<String>();
                for(String dataType : dataTypes) {
                    if(CompareUtils.areSchemeAndDataTypeSame(operation.getResponseScheme(), dataType)) {
                        restrictedDataTypes.add(dataType);
                    }
                }
                taskOutputsTable.setAvailableDataTypes(restrictedDataTypes);
            } else {
                taskOutputsTable.setAvailableDataTypes(dataTypes);
            }
        } else {
            taskOutputsTable.setAvailableDataTypes(dataTypes);
        }
    }
}


