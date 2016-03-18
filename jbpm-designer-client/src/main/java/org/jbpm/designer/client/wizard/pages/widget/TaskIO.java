package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskIO extends Composite implements HasModel<Task>, HasValue<Task> {

    interface TaskIOBinder extends UiBinder<Widget, TaskIO> {
    }

    private static TaskIOBinder uiBinder = GWT.create(TaskIOBinder.class);

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    private ProcessTasksPageView.Presenter presenter;

    @UiField
    FlexTable variables;

    @UiField
    InputRow output;

    private Map<Variable, CheckBox> variableMap = new HashMap<Variable, CheckBox>();

    public TaskIO() {
        initWidget(uiBinder.createAndBindUi(this));
        dataBinder.bind(output, "output");

        dataBinder.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                presenter.firePageChangedEvent();
            }
        });
    }

    public void init(ProcessTasksPageView.Presenter presenter) {
        this.presenter = presenter;
    }

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
                    Task model = dataBinder.getModel();
                    model.setInputs(selected);
                    setModel(model);
                }
            });
            variables.setWidget(newRow, 0, checkBox);
            variables.setWidget(newRow, 1, new Text(variable.getName() + ":" + variable.getDataType()));
            variableMap.put(variable, checkBox);
        }
    }

    public void setSelectedVariables(List<Variable> vars) {
        for(Variable variable : variableMap.keySet()) {
            if(vars.contains(variable)) {
                variableMap.get(variable).setValue(true, true);
            } else {
                variableMap.get(variable).setValue(false, true);
            }
        }
    }

    public void setOutputVariable(Variable variable) {
        output.setValue(variable, true);
    }

    @Override
    public Task getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Task task) {
        dataBinder.setModel(task);
        if (task.getInputs() != null) {
            for (Variable variable : task.getInputs()) {
                variableMap.get(variable).setValue(true);
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
        dataBinder.bind(output, "output").getModel();
    }
}


