package org.jbpm.designer.client.shared;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class Task {

    private String name;

    private Variable input;

    private Variable output;

    private User responsibleHuman;

    private Group responsibleGroup;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Variable getInput() {
        return input;
    }

    public void setInput(Variable input) {
        this.input = input;
    }

    public Variable getOutput() {
        return output;
    }

    public void setOutput(Variable output) {
        this.output = output;
    }

    public User getResponsibleHuman() {
        return responsibleHuman;
    }

    public void setResponsibleHuman(User responsibleHuman) {
        this.responsibleHuman = responsibleHuman;
    }

    public Group getResponsibleGroup() {
        return responsibleGroup;
    }

    public void setResponsibleGroup(Group responsibleGroup) {
        this.responsibleGroup = responsibleGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        if (input != null ? !input.equals(task.input) : task.input != null) return false;
        if (output != null ? !output.equals(task.output) : task.output != null) return false;
        if (responsibleHuman != null ? !responsibleHuman.equals(task.responsibleHuman) : task.responsibleHuman != null)
            return false;
        return responsibleGroup != null ? responsibleGroup.equals(task.responsibleGroup) : task.responsibleGroup == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (input != null ? input.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        result = 31 * result + (responsibleHuman != null ? responsibleHuman.hashCode() : 0);
        result = 31 * result + (responsibleGroup != null ? responsibleGroup.hashCode() : 0);
        return result;
    }
}
