package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Radio;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jbpm.designer.client.shared.Group;
import org.jbpm.designer.client.shared.Task;
import org.jbpm.designer.client.shared.Task;
import org.jbpm.designer.client.shared.User;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;

@Dependent
public class TaskDetail extends Composite implements HasModel<Task> {

    @Inject
    @AutoBound
    private DataBinder<Task> dataBinder;

    @Bound
    private TextBox name = new TextBox();

    @Bound
    private InputRow input = new InputRow();

    @Bound
    private InputRow output = new InputRow();

    @Bound
    private ValueListBox<User> responsibleHuman = new ValueListBox<User>(new Renderer<User>() {
        @Override
        public String render(User user) {
            return user.getName();
        }

        @Override
        public void render(User user, Appendable appendable) throws IOException {
            String s = render(user);
            appendable.append(s);
        }
    });

    @Bound
    private ValueListBox<Group> responsibleGroup = new ValueListBox<Group>(new Renderer<Group>() {
        @Override
        public String render(Group group) {
            return group.getName();
        }

        @Override
        public void render(Group group, Appendable appendable) throws IOException {
            String s = render(group);
            appendable.append(s);
        }
    });

    public TaskDetail() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(name);
        panel.add(input);
        panel.add(output);
        panel.add(responsibleHuman);
        panel.add(responsibleGroup);
        initWidget(panel);
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
