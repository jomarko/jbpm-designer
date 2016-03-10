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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.*;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.shared.*;

import javax.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class TaskDetail extends Composite implements HasModel<Task> {

    interface TaskDetailBinder
            extends
            UiBinder<Widget, TaskDetail> {
    }

    private static TaskDetailBinder uiBinder = GWT.create(TaskDetailBinder.class);

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    @UiField(provided = true)
    ValueListBox<String> taskType = new ValueListBox<String>(new ToStringRenderer());

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

    @UiField
    FieldSet operationWrapper;

    @UiField
    HelpBlock operationHelp;

    @UiField(provided = true)
    ValueListBox<String> operation = new ValueListBox<String>(new ToStringRenderer());

    public TaskDetail() {
        initWidget(uiBinder.createAndBindUi(this));
        bindDataBinder();

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

    public void unbind() {
        dataBinder.unbind();
    }

    public void rebind() {
        bindDataBinder();
    }

    public void setPropertyChangeChandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }

    public void showHumanDetails() {
        operationWrapper.setVisible(false);
        participantWrapper.setVisible(true);
    }

    public void showServiceDetails() {
        operationWrapper.setVisible(true);
        participantWrapper.setVisible(false);
    }

    public void setNameHelpVisibility(boolean value) {
        nameHelp.setVisible(value);
    }

    public void setParticipantHelpVisibility(boolean value) {
        participantHelp.setVisible(value);
    }

    public void setOperationHelpVisibility(boolean value) {
        operationHelp.setVisible(value);
    }

    private void bindDataBinder() {
        dataBinder.bind(name, "name")
                .bind(responsibleHuman, "responsibleHuman")
                .bind(responsibleGroup, "responsibleGroup")
                .bind(taskType, "taskType")
                .bind(operation, "operation")
                .getModel();
    }
}
