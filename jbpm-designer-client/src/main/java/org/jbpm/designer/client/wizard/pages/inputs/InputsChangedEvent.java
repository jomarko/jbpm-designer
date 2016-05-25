package org.jbpm.designer.client.wizard.pages.inputs;

import org.jbpm.designer.model.Variable;

import java.util.List;

public class InputsChangedEvent {
    List<Variable> inputs;

    public List<Variable> getInputs() {
        return inputs;
    }

    public void setInputs(List<Variable> inputs) {
        this.inputs = inputs;
    }
}
