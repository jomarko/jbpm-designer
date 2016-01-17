package org.jbpm.designer.client.wizard.pages.widget;

import org.jbpm.designer.client.shared.Variable;

public class InputsTable extends DeletableFlexTable<Variable> {

    @Override
    public WidgetWithModel<Variable> getNewRowWidget() {
        InputRow widget = new InputRow();
        Variable variable = new Variable(Variable.VariableType.INPUT);
        variable.setDataType("boolean");
        widget.setModel(variable);
        return widget;
    }
}
