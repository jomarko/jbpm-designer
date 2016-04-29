package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;

@Bindable
@Portable
public class Task {

    private static int lastId = 1;

    private int id;

    private String name;

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

        if (getId() != task.getId()) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getInputs() != null ? !getInputs().equals(task.getInputs()) : task.getInputs() != null) return false;
        return getOutput() != null ? getOutput().equals(task.getOutput()) : task.getOutput() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getInputs() != null ? getInputs().hashCode() : 0);
        result = 31 * result + (getOutput() != null ? getOutput().hashCode() : 0);
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
