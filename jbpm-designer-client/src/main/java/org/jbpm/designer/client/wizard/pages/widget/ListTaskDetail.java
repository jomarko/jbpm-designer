package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jbpm.designer.client.shared.Task;


public class ListTaskDetail extends Composite implements WidgetWithModel<Task> {

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    private TextBox name = new TextBox();

    public ListTaskDetail() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(name);
        initWidget(panel);
        dataBinder.bind(name, "name");
    }

    @Override
    public Task getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Task task) {
        dataBinder.setModel(task);
    }
}
