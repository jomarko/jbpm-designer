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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;
import org.jbpm.designer.client.wizard.util.DefaultValues;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.Operation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class TaskDetail extends Composite implements HasModel<Task> {

    protected String SERVICE_TYPE = "Service";
    protected String HUMAN_TYPE = "Human";

    interface TaskDetailBinder
            extends
            UiBinder<Widget, TaskDetail> {
    }

    private static TaskDetailBinder uiBinder = GWT.create(TaskDetailBinder.class);

    @UiField(provided = true)
    ValueListBox<String> taskType = new ValueListBox<String>(new ToStringRenderer());

    @UiField
    Form taskDetailForm;

    @Inject
    protected HumanTaskDetail humanTaskDetail;

    @Inject
    protected ServiceTaskDetail serviceTaskDetail;

    private ProcessTasksPageView.Presenter presenter;

    public TaskDetail() {
        initWidget(uiBinder.createAndBindUi(this));

        taskType.setValue(SERVICE_TYPE, true);
        taskType.setValue(HUMAN_TYPE, true);

        taskType.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                if(presenter != null) {
                    presenter.selectedWidgetModelChanged(getModel());
                }
            }
        });
    }


    @PostConstruct
    public void initialize() {
        taskDetailForm.add( humanTaskDetail );
        taskDetailForm.add( serviceTaskDetail );
    }

    public void init(ProcessTasksPageView.Presenter presenter) {
        this.presenter = presenter;
        serviceTaskDetail.setModel(new DefaultValues().getDefaultServiceTask());
        humanTaskDetail.setModel(new DefaultValues().getDefaultHumanTask());
    }

    @Override
    public Task getModel() {
        if(taskType.getValue().equals(HUMAN_TYPE)) {
            return humanTaskDetail.getModel();
        } else {
            return serviceTaskDetail.getModel();
        }
    }

    @Override
    public void setModel(Task task) {
        if(task instanceof HumanTask) {
            humanTaskDetail.setModel((HumanTask) task);
        } else {
            serviceTaskDetail.setModel((ServiceTask) task);
        }
    }

    public void unbind() {
        serviceTaskDetail.unbind();
        humanTaskDetail.unbind();
    }

    public void rebind() {
        if(taskType.getValue().equals(HUMAN_TYPE)) {
            humanTaskDetail.bindDataBinder();
        } else {
            serviceTaskDetail.bindDataBinder();
        }
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        humanTaskDetail.setPropertyChangeHandler(handler);
        serviceTaskDetail.setPropertyChangeHandler(handler);
    }

    public void addHumanParticipants(List<User> participants) {
        humanTaskDetail.addHumanParticipants(participants);
    }

    public void addGroupParticipants(List<Group> participants) {
        humanTaskDetail.addGroupParticipants(participants);
    }

    public void showHumanDetails() {
        humanTaskDetail.setVisible(true);
        serviceTaskDetail.setVisible(false);
        taskType.setValue(HUMAN_TYPE, false);
    }

    public void showServiceDetails() {
        humanTaskDetail.setVisible(false);
        serviceTaskDetail.setVisible(true);
        taskType.setValue(SERVICE_TYPE, false);
    }

    public void setNameHelpVisibility(boolean value) {
        humanTaskDetail.setNameHelpVisibility(value);
        serviceTaskDetail.setNameHelpVisibility(value);
    }

    public void setParticipantHelpVisibility(boolean value) {
        humanTaskDetail.setParticipantHelpVisibility(value);
    }

    public void setOperationHelpVisibility(boolean value) {
        serviceTaskDetail.setOperationHelpVisibility(value);
    }

    public void addAvailableOperation(Operation operation) {
        serviceTaskDetail.addAvailableOperation(operation);
    }

    public void setVariablesForParameterMapping(List<Variable> variables) {
        serviceTaskDetail.setVariablesForParameterMapping(variables);
    }
}
