package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.designer.model.operation.Operation;

@Bindable
@Portable
public class ServiceTask extends Task{

    public ServiceTask() {
    }

    public ServiceTask(String name) {
        super(name);
    }

    private Operation operation;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
