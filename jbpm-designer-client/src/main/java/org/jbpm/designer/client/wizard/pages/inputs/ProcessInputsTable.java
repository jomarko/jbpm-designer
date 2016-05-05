package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.model.Variable;

import javax.inject.Inject;
import java.util.List;

@Templated("ProcessInputsTable.html#widget")
public class ProcessInputsTable extends Composite{

    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<Variable, ProcessInputRow> inputs;

    public List<Variable> getVariables() {
        return inputs.getValue();
    }

    public void addVariable(Variable variable, List<String> dataTypes) {
        inputs.getValue().add(variable);
        ProcessInputRow widget = inputs.getWidget(inputs.getValue().size() - 1);
        widget.setAcceptableDataTypes(dataTypes);
        widget.setParentWidget(this);
    }

    public void removeVariable(Variable variable) {
        inputs.getValue().remove(variable);
        ValueChangeEvent.fire(inputs, inputs.getValue());
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

    public void addValueChangeHandler(ValueChangeHandler<List<Variable>> handler) {
        inputs.addValueChangeHandler(handler);
    }

    public boolean isVariableNameDuplicate(String name) {
        int occurrences = 0;
        for (Variable anotherVariable : inputs.getValue()) {
            if (anotherVariable.getName().compareTo(name) == 0) {
                occurrences++;
            }
        }

        return occurrences > 1;
    }
}
