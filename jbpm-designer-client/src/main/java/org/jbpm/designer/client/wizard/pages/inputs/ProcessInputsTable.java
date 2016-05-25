package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.wizard.util.DefaultValues;
import org.jbpm.designer.model.Variable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Templated("ProcessInputsTable.html#widget")
public class ProcessInputsTable extends Composite {

    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<Variable, ProcessInputRow> inputs;

    @Inject
    @DataField
    Button addButton;

    private DefaultValues defaultValues = new DefaultValues();

    private List<String> dataTypes;

    @PostConstruct
    public void initialize() {
        dataTypes = new ArrayList<String>();
        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                addVariable(defaultValues.getDefaultVariable(), dataTypes);
                fireInputsChanged();
            }
        });
    }

    public List<Variable> getVariables() {
        return inputs.getValue();
    }

    public void addVariable(Variable variable, List<String> dataTypes) {
            inputs.getValue().add(variable);
            ProcessInputRow widget = inputs.getWidget(inputs.getValue().size() - 1);
            widget.setAcceptableDataTypes(dataTypes);
            widget.setParentWidget(this);
    }

    public void setVariableProblemMarkVisibility(Variable variable, boolean visible) {
        ProcessInputRow widget = inputs.getWidget(variable);
        if(widget != null) {
            if(visible) {
                widget.showAsterisk(true);
            } else {
                widget.showAsterisk(false);
            }
        }
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Variable>> handler) {
        return inputs.addValueChangeHandler(handler);
    }

    public ListWidget<Variable, ProcessInputRow> getListWidget() {
        return inputs;
    }

    public void deleteVariable(Variable variable) {
        inputs.getValue().remove(variable);
        for(int i = 0; i < inputs.getWidgetCount(); i++) {
            ProcessInputRow widget = inputs.getWidget(i);
            widget.setAcceptableDataTypes(dataTypes);
            widget.setParentWidget(this);
        }
        fireInputsChanged();
    }

    public void clear() {
        inputs.setValue(new ArrayList<Variable>(), true);
        fireInputsChanged();
    }

    public void setAvailableDataTypes(List<String> availableDataTypes) {
        dataTypes.clear();
        if(availableDataTypes != null) {
            dataTypes.addAll(availableDataTypes);
        }
    }

    public void fireInputsChanged() {
        ValueChangeEvent.fire(inputs, inputs.getValue());
    }
}
