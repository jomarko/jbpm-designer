package org.jbpm.designer.client.wizard.pages.widget;


import org.jbpm.designer.client.shared.Group;
import org.jbpm.designer.client.shared.Participant;

public class GroupsTable extends DeletableFlexTable<Participant> {

    @Override
    public WidgetWithModel<Participant> getNewRowWidget() {
        ListGroupDetail widget = new ListGroupDetail();
        widget.setModel(new Group(""));
        return widget;
    }
}
