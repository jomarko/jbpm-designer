package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;

@Bindable
@Portable
public class Task {

    public static final String HUMAN_TYPE = "Human";
    public static final String SERVICE_TYPE = "Service";

    private static int lastId = 1;

    private int id;

    private String name;

    private Participant responsibleHuman;

    private Participant responsibleGroup;

    private String operation;

    private String taskType;

    private Condition condition;

    private List<Variable> inputs;

    private Variable output;

    public Task() {
        id = lastId++;
    }

    public Task(String name) {
        id = lastId++;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Participant getResponsibleHuman() {
        return responsibleHuman;
    }

    public void setResponsibleHuman(Participant responsibleHuman) {
        this.responsibleHuman = responsibleHuman;
    }

    public Participant getResponsibleGroup() {
        return responsibleGroup;
    }

    public void setResponsibleGroup(Participant responsibleGroup) {
        this.responsibleGroup = responsibleGroup;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<Variable> getInputs() {
        return inputs;
    }

    public void setInputs(List<Variable> inputs) {
        this.inputs = inputs;
    }

    public Variable getOutput() {
        return output;
    }

    public void setOutput(Variable output) {
        this.output = output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getResponsibleHuman() != null ? !getResponsibleHuman().equals(task.getResponsibleHuman()) : task.getResponsibleHuman() != null)
            return false;
        if (getResponsibleGroup() != null ? !getResponsibleGroup().equals(task.getResponsibleGroup()) : task.getResponsibleGroup() != null)
            return false;
        if (getOperation() != null ? !getOperation().equals(task.getOperation()) : task.getOperation() != null)
            return false;
        if (getCondition() != null ? !getCondition().equals(task.getCondition()) : task.getCondition() != null)
            return false;
        if (getInputs() != null ? !getInputs().equals(task.getInputs()) : task.getInputs() != null)
            return false;
        if (getOutput() != null ? !getOutput().equals(task.getOutput()) : task.getOutput() != null)
            return false;
        return getTaskType() != null ? getTaskType().equals(task.getTaskType()) : task.getTaskType() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (getResponsibleHuman() != null ? getResponsibleHuman().hashCode() : 0);
        result = 31 * result + (getResponsibleGroup() != null ? getResponsibleGroup().hashCode() : 0);
        result = 31 * result + (getOperation() != null ? getOperation().hashCode() : 0);
        result = 31 * result + (getTaskType() != null ? getTaskType().hashCode() : 0);
        result = 31 * result + (getCondition() != null ? getCondition().hashCode() : 0);
        result = 31 * result + (getInputs() != null ? getInputs().hashCode() : 0);
        result = 31 * result + (getOutput() != null ? getOutput().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                "name='" + name + '\'' +
                ", responsibleHuman=" + responsibleHuman +
                ", responsibleGroup=" + responsibleGroup +
                ", operation='" + operation + '\'' +
                ", taskType='" + taskType + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }
}
