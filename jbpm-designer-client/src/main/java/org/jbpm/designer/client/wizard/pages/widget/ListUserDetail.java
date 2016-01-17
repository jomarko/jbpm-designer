package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jbpm.designer.client.shared.Participant;


public class ListUserDetail extends Composite implements WidgetWithModel<Participant> {

    private DataBinder<Participant> dataBinder = DataBinder.forType(Participant.class);

    private TextBox name = new TextBox();

    public ListUserDetail() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(name);
        initWidget(panel);
        dataBinder.bind(name, "name");
    }

    @Override
    public Participant getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Participant participant) {
        dataBinder.setModel(participant);
    }
}
