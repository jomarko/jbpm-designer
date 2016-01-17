/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jbpm.designer.client.shared.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Dependent
public class TaskDetail extends Composite implements HasModel<Task> {

    @Inject
    @AutoBound
    private DataBinder<Task> dataBinder;

    @Bound
    private ValueListBox<String> taskType = new ValueListBox<String>(new ToStringRenderer());

    @Bound
    private TextBox name = new TextBox();

    private InputRow outputAdditional = new InputRow();

    private Button addButton = new Button("add");

    @Bound
    private ValueListBox<Variable> input = new ValueListBox<Variable>(new ToStringRenderer());

    @Bound
    private ValueListBox<Variable> output = new ValueListBox<Variable>(new ToStringRenderer());

    @Bound
    private ValueListBox<User> responsibleHuman = new ValueListBox<User>(new ToStringRenderer());

    @Bound
    private ValueListBox<Group> responsibleGroup = new ValueListBox<Group>(new ToStringRenderer());

    @Bound
    private ValueListBox<String> operation = new ValueListBox<String>(new ToStringRenderer());

    public TaskDetail() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(taskType);
        panel.add(name);
        panel.add(input);
        panel.add(output);
        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(outputAdditional.getModel().getName() != null && !outputAdditional.getModel().getName().isEmpty()) {
                    output.setValue(outputAdditional.getModel(), true);
                }
            }
        });
        HorizontalPanel addOutputPanel = new HorizontalPanel();
        addOutputPanel.add(outputAdditional);
        addOutputPanel.add(addButton);
        panel.add(addOutputPanel);
        panel.add(responsibleHuman);
        panel.add(responsibleGroup);
        panel.add(operation);
        // temporary solution
        operation.setValue("operationA");
        operation.setValue("operationB");

        taskType.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                if(valueChangeEvent.getValue().equals(Task.HUMAN_TYPE)) {
                    showHumanDetails();
                }else if(valueChangeEvent.getValue().equals(Task.SERVICE_TYPE)) {
                    showServiceDetails();
                }
            }
        });

        taskType.setValue(Task.SERVICE_TYPE, true);
        taskType.setValue(Task.HUMAN_TYPE, true);

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

    public void setHumanParticipants(List<User> users) {
        responsibleHuman.setAcceptableValues(users);
    }

    public void setGroupParticipants(List<Group> groups) {
        responsibleGroup.setAcceptableValues(groups);
    }

    public void setVariables(List<Variable> variables) {
        input.setAcceptableValues(variables);
        output.setAcceptableValues(variables);
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void rebind() {
        dataBinder.bind(name, "name")
                .bind(input, "input")
                .bind(output, "output")
                .bind(responsibleHuman, "responsibleHuman")
                .bind(responsibleGroup, "responsibleGroup")
                .bind(taskType, "taskType")
                .bind(operation, "operation");
    }

    public void setPropertyChangeChandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }

    public void showHumanDetails() {
        operation.setVisible(false);
        responsibleHuman.setVisible(true);
        responsibleGroup.setVisible(true);
    }

    public void showServiceDetails() {
        operation.setVisible(true);
        responsibleHuman.setVisible(false);
        responsibleGroup.setVisible(false);
    }
}
