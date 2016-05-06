package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jbpm.designer.client.wizard.pages.inputs.ProcessInputsTable;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskIO extends Composite implements HasModel<Task>, HasValue<Task> {

    interface TaskIOBinder extends UiBinder<Widget, TaskIO> {
    }

    private static TaskIOBinder uiBinder = GWT.create(TaskIOBinder.class);

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    @UiField
    FlexTable variables;

    @UiField
    FieldSet outputsFieldSet;

    @Inject
    private ProcessInputsTable outputs;

    @UiField
    Button addButton;

    private Map<Variable, CheckBox> variableCheckBoxes = new HashMap<Variable, CheckBox>();

    private List<String> dataTypes = new ArrayList<String>();

    public TaskIO() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    public void initializeView() {
        outputsFieldSet.add(outputs);
        dataBinder.bind(outputs.getListWidget(), "outputs");
        dataTypes.clear();
        dataTypes.add("String");
        dataTypes.add("Float");
        dataTypes.add("Boolean");
    }

    @EventHandler("addButton")
    public void handleAddButton(ClickEvent e) {
        Variable defaultModel = new Variable();
        defaultModel.setVariableType(Variable.VariableType.OUTPUT);
        defaultModel.setName("");
        defaultModel.setDataType("String");
        outputs.addVariable(defaultModel, dataTypes);
    }

    public void setPropertyChangeChandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }

    public void setAcceptableValues(List<Variable> vars) {
        variables.removeAllRows();
        variableCheckBoxes.clear();
        for(final Variable variable : vars) {
            int newRow = variables.getRowCount();
            CheckBox checkBox = new CheckBox();
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    List<Variable> selected = new ArrayList<Variable>();
                    for(Variable variable : variableCheckBoxes.keySet()) {
                        if(variableCheckBoxes.get(variable).getValue()) {
                            selected.add(variable);
                        }
                    }
                    Task model = dataBinder.getModel();
                    model.setInputs(selected);
                    setModel(model);
                }
            });
            variables.setWidget(newRow, 0, checkBox);
            variables.setWidget(newRow, 1, new Text(variable.getName() + ":" + variable.getDataType()));
            variableCheckBoxes.put(variable, checkBox);
        }
    }

    @Override
    public Task getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Task task) {
        dataBinder.setModel(task);
        if (task.getInputs() != null) {
            List<Variable> taskInputs = task.getInputs();
            for (Variable variable : task.getInputs()) {
                if(variableCheckBoxes.containsKey(variable)) {
                    variableCheckBoxes.get(variable).setValue(true);
                } else {
                    taskInputs.remove(variable);
                }
            }
            if(taskInputs.size() != task.getInputs().size()) {
                task.setInputs(taskInputs);
                setValue(task, true);
            }
        }
    }

    @Override
    public Task getValue() {
        return getModel();
    }

    @Override
    public void setValue(Task task) {
        setValue(task, false);
    }

    @Override
    public void setValue(Task task, boolean fireEvent) {
        if(fireEvent) {
            ValueChangeEvent.fire(this, task);
        }
        setModel(task);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Task> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void rebind() {
        dataBinder.bind(outputs.getListWidget(), "outputs").getModel();
    }
}


