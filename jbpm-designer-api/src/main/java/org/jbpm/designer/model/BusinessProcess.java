package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.List;

@Portable
public class BusinessProcess {

    private List<Variable> variables;

    private List<Task> tasks;

    public BusinessProcess() {
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
