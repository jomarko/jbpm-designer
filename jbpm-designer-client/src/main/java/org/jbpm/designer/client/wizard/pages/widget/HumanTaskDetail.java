package org.jbpm.designer.client.wizard.pages.widget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Group;
import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.User;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class HumanTaskDetail extends Composite implements HasModel<HumanTask> {

    private DataBinder<HumanTask> dataBinder = DataBinder.forType(HumanTask.class);

    interface HumanTaskDetailBinder
            extends
            UiBinder<Widget, HumanTaskDetail> {
    }

    private static HumanTaskDetailBinder uiBinder = GWT.create(HumanTaskDetailBinder.class);


    private List<User> acceptableUsers;
    private List<Group> acceptableGroups;

    @UiField
    TextBox name;

    @UiField
    HelpBlock nameHelp;

    @UiField
    FieldSet participantWrapper;

    @UiField(provided = true)
    ValueListBox<User> responsibleHuman = new ValueListBox<User>(new ToStringRenderer());

    @UiField(provided = true)
    ValueListBox<Group> responsibleGroup = new ValueListBox<Group>(new ToStringRenderer());

    @UiField
    HelpBlock participantHelp;

    public HumanTaskDetail() {
        initWidget(uiBinder.createAndBindUi(this));
        bindDataBinder();

        acceptableUsers = new ArrayList<User>();
        acceptableGroups = new ArrayList<Group>();
    }

    public void addHumanParticipants(List<User> users) {
        acceptableUsers.addAll(users);
        responsibleHuman.setAcceptableValues(acceptableUsers);
    }

    public void addGroupParticipants(List<Group> groups) {
        acceptableGroups.addAll(groups);
        responsibleGroup.setAcceptableValues(acceptableGroups);
    }

    public void bindDataBinder() {
        dataBinder.bind(name, "name")
                .bind(responsibleHuman, "responsibleHuman")
                .bind(responsibleGroup, "responsibleGroup")
                .getModel();
    }

    @Override
    public HumanTask getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(HumanTask humanTask) {
        dataBinder.setModel(humanTask);
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }

    public void setNameHelpVisibility(boolean value) {
        nameHelp.setVisible( value );
    }

    public void setParticipantHelpVisibility(boolean value) {
        participantHelp.setVisible( value );
    }
}
