package org.jbpm.designer.client.wizard.pages.participants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.shared.Group;
import org.jbpm.designer.client.shared.Participant;
import org.jbpm.designer.client.shared.User;
import org.jbpm.designer.client.wizard.pages.widget.*;

import javax.enterprise.context.Dependent;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class ProcessParticipantsPageViewImpl extends Composite
                                             implements ProcessParticipantsPageView,
                                                        DeletableFlexTable.RowsHandler<Participant>{

    interface ProcessParticipantsPageViewImplBinder
            extends
            UiBinder<Widget, ProcessParticipantsPageViewImpl> {
    }

    private static ProcessParticipantsPageViewImplBinder uiBinder = GWT.create(ProcessParticipantsPageViewImplBinder.class);

    private Map<ListUserDetail, User> usersMapper = new HashMap<ListUserDetail, User>();
    private Map<ListGroupDetail, Group> groupsMapper = new HashMap<ListGroupDetail, Group>();


    public ProcessParticipantsPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        users.registerRowsHandler(this);
        groups.registerRowsHandler(this);
    }

    @UiField
    UsersTable users;

    @UiField
    GroupsTable groups;

    @Override
    public void addedRow(WidgetWithModel<Participant> widget) {
        if(widget.getModel() instanceof User) {
            usersMapper.put((ListUserDetail) widget, (User) widget.getModel());
        }

        if(widget.getModel() instanceof Group) {
            groupsMapper.put((ListGroupDetail) widget, (Group) widget.getModel());
        }
    }

    @Override
    public void rowSelected(WidgetWithModel<Participant> widget) {
        if(widget.getModel() instanceof User) {

        }

        if(widget.getModel() instanceof Group) {

        }
    }

    @Override
    public void rowDeleted(WidgetWithModel<Participant> widget) {
        if(widget.getModel() instanceof User) {
            usersMapper.remove(widget);
        }

        if(widget.getModel() instanceof Group) {
            usersMapper.remove(widget);
        }
    }
}
