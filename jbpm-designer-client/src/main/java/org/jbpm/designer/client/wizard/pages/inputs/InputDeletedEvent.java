package org.jbpm.designer.client.wizard.pages.inputs;

import org.jbpm.designer.model.Variable;

import java.util.List;

public class InputDeletedEvent {
    private Variable deletedInput;

    public Variable getDeletedInput() {
        return deletedInput;
    }

    public void setDeletedInput(Variable deletedInput) {
        this.deletedInput = deletedInput;
    }
}
