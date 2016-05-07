package org.jbpm.designer.client.wizard.util;

import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Variable;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
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
        serviceTask.setInputs(new ArrayList<Variable>());
        return serviceTask;
    }

    public HumanTask getDefaultHumanTask() {
        HumanTask task = new HumanTask("");
        task.setOutputs(new ArrayList<Variable>());
        task.setInputs(new ArrayList<Variable>());
        return task;
    }

    public List<String> getDefaultDataTypes() {
        List<String> dataTypes = new ArrayList<String>();
        dataTypes.add("Boolean");
        dataTypes.add("Float");
        dataTypes.add("String");
        return dataTypes;
    }
}
