package org.jbpm.designer.client.wizard.pages.tasks;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.designer.model.Variable;

@Bindable
@Portable
public class TaskInputEntry {

    private Variable variable;

    private boolean selected;

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
