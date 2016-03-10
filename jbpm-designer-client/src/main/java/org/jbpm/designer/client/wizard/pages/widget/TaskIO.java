package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jbpm.designer.client.shared.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskIO extends Composite {
    interface TaskIOBinder
            extends
            UiBinder<Widget, TaskIO> {
    }

    public interface TaskInputsChanged {
        void actualTaskInputs(List<Variable> actualVariables);

        void actualTaskOutput(Variable variable);
    }

    private static TaskIOBinder uiBinder = GWT.create(TaskIOBinder.class);

    public TaskIO() {
        initWidget(uiBinder.createAndBindUi(this));
        output.addValueChangeHandler(new ValueChangeHandler<Variable>() {
            @Override
            public void onValueChange(ValueChangeEvent<Variable> valueChangeEvent) {
                taskInputsChangedHandler.actualTaskOutput(valueChangeEvent.getValue());
            }
        });
    }

    private TaskInputsChanged taskInputsChangedHandler;

    @UiField
    FlexTable variables;

    @UiField
    InputRow output;

    private Map<Variable, CheckBox> variableMap = new HashMap<Variable, CheckBox>();

    public void setAcceptableValues(List<Variable> vars) {
        variables.removeAllRows();
        variableMap.clear();
        for(final Variable variable : vars) {
            int newRow = variables.getRowCount();
            CheckBox checkBox = new CheckBox();
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    List<Variable> selected = new ArrayList<Variable>();
                    for(Variable variable : variableMap.keySet()) {
                        if(variableMap.get(variable).getValue()) {
                            selected.add(variable);
                        }
                    }

                    taskInputsChangedHandler.actualTaskInputs(selected);
                }
            });
            variables.setWidget(newRow, 0, checkBox);
            variables.setWidget(newRow, 1, new Text(variable.getName() + ":" + variable.getDataType()));
            variableMap.put(variable, checkBox);
        }
    }

    public void setTaskInputsChangedHandler(TaskInputsChanged handler) {
        this.taskInputsChangedHandler = handler;
    }

    public void setSelectedVariables(List<Variable> vars) {
        for(Variable variable : variableMap.keySet()) {
            if(vars.contains(variable)) {
                variableMap.get(variable).setValue(true);
            } else {
                variableMap.get(variable).setValue(false);
            }
        }
    }

    public void setOutputVariable(Variable variable) {
        output.setValue(variable, true);
    }
}


