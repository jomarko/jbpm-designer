package org.jbpm.designer.model;


import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;

@Portable
@Bindable
public class HumanTask extends Task {

    public HumanTask() {
    }

    public HumanTask(String name) {
        super(name);
    }

    private Participant responsibleHuman;

    private Participant responsibleGroup;

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
}
