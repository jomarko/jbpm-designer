package org.jbpm.designer.client.wizard.util;

import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.Operation;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class DefaultValues {

    public Variable getDefaultVariable() {
        Variable variable = new Variable();
        variable.setName("");
        variable.setDataType("String");
        return variable;
    }

    public ServiceTask getDefaultServiceTask() {
        ServiceTask serviceTask = new ServiceTask("");
        serviceTask.setOutputs(new ArrayList<Variable>());
        serviceTask.setInputs(new HashMap<String, Variable>());
        return serviceTask;
    }

    public HumanTask getDefaultHumanTask() {
        HumanTask task = new HumanTask("");
        task.setOutputs(new ArrayList<Variable>());
        task.setInputs(new HashMap<String, Variable>());
        return task;
    }
}
