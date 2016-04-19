package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.List;
import java.util.Map;

@Portable
public class BusinessProcess {

    private String processName;

    private String processDocumentation;

    private StandardEvent startEvent;

    private List<Variable> variables;

    private Map<Integer, List<Task>> tasks;

    private Map<Integer, List<Condition>> conditions;

    public BusinessProcess() {
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessDocumentation() {
        return processDocumentation;
    }

    public void setProcessDocumentation(String processDocumentation) {
        this.processDocumentation = processDocumentation;
    }

    public StandardEvent getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(StandardEvent startEvent) {
        this.startEvent = startEvent;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public Map<Integer, List<Task>> getTasks() {
        return tasks;
    }

    public void setTasks(Map<Integer, List<Task>> tasks) {
        this.tasks = tasks;
    }

    public Map<Integer, List<Condition>> getConditions() {
        return conditions;
    }

    public void setConditions(Map<Integer, List<Condition>> conditions) {
        this.conditions = conditions;
    }
}
