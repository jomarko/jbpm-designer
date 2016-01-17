package org.jbpm.designer.client.wizard.pages.widget;

import org.jbpm.designer.client.shared.Participant;
import org.jbpm.designer.client.shared.User;


public class UsersTable extends DeletableFlexTable<Participant> {

    @Override
    public WidgetWithModel<Participant> getNewRowWidget() {
        ListUserDetail widget = new ListUserDetail();
        widget.setModel(new User(""));
        return widget;
    }

}
