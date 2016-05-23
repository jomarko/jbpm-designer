package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;
import java.util.Map;

@Bindable
@Portable
public class Task {

    private static int lastId = 1;

    private int id;

    private String name;

    private Map<String, Variable> inputs;

    private List<Variable> outputs;

    private boolean terminateHere;

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

    public Map<String, Variable> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Variable> inputs) {
        this.inputs = inputs;
    }

    public List<Variable> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Variable> outputs) {
        this.outputs = outputs;
    }

    public boolean isTerminateHere() {
        return terminateHere;
    }

    public void setTerminateHere(boolean terminateHere) {
        this.terminateHere = terminateHere;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getId() != task.getId()) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getInputs() != null ? !getInputs().equals(task.getInputs()) : task.getInputs() != null) return false;
        if (isTerminateHere() != task.isTerminateHere()) return false;
        return getOutputs() != null ? getOutputs().equals(task.getOutputs()) : task.getOutputs() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getInputs() != null ? getInputs().hashCode() : 0);
        result = 31 * result + (getOutputs() != null ? getOutputs().hashCode() : 0);
        if(isTerminateHere()) {
            result += 31;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
