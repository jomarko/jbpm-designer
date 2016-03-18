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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPageView;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class ConditionWidget extends Composite implements HasModel<Task>, HasValue<Task> {

    interface ConditionWidgetBinder
            extends
            UiBinder<Widget, ConditionWidget> {
    }

    private static ConditionWidgetBinder uiBinder = GWT.create(ConditionWidgetBinder.class);

    DataBinder<Task> binder = DataBinder.forType(Task.class);

    private ProcessTasksPageView.Presenter presenter;

    @UiField(provided = true)
    ValueListBox<Variable> variable = new ValueListBox<Variable>(new ToStringRenderer());

    @UiField
    HelpBlock variableHelp;

    @UiField(provided = true)
    ValueListBox<String> constraint = new ValueListBox<String>(new ToStringRenderer());

    @UiField
    HelpBlock constraintHelp;

    @UiField
    TextBox constraintValue;

    @UiField
    HelpBlock constraintValueHelp;

    @UiField
    CheckBox constraintSatisfied;

    public ConditionWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        binder.bind(variable, "condition.constraint.variable")
            .bind(constraint, "condition.constraint.constraint")
            .bind(constraintValue, "condition.constraint.constraintValue")
            .bind(constraintSatisfied, "condition.executeIfConstraintSatisfied")
            .getModel();

        variable.addValueChangeHandler(new ValueChangeHandler<Variable>() {
            @Override
            public void onValueChange(ValueChangeEvent<Variable> valueChangeEvent) {
                constraint.setValue("");
                constraint.setAcceptableValues(getConstraints(valueChangeEvent.getValue()));
            }
        });

        binder.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                presenter.firePageChangedEvent();
            }
        });
    }

    public void init(ProcessTasksPageView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Task getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(Task task) {
        binder.setModel(task);
    }

    @Override
    public Task getValue() {
        return getModel();
    }

    @Override
    public void setValue(Task task) {
        setValue(task, false);
    }

    @Override
    public void setValue(Task task, boolean fireEvents) {
        setModel(task);
        if(fireEvents) {
            ValueChangeEvent.fire(this, task);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Task> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void setVariables(List<Variable> variables) {
        variable.setAcceptableValues(variables);
    }

    public void unbind() {
        binder.unbind();
    }

    public void rebind() {
        binder.bind(variable, "condition.constraint.variable")
                .bind(constraint, "condition.constraint.constraint")
                .bind(constraintValue, "condition.constraint.constraintValue")
                .bind(constraintSatisfied, "condition.executeIfConstraintSatisfied");
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        binder.addPropertyChangeHandler(handler);
    }

    public void setVariableHelpVisibility(boolean value) {
        variableHelp.setVisible(value);
    }

    public void setConstraintValueHelpVisibility(boolean value) {
        constraintValueHelp.setVisible(value);
    }

    public void setConstraintHelpVisibility(boolean value) {
        constraintHelp.setVisible(value);
    }

    private List<String> getConstraints(Variable var) {
        List<String> constraints = new ArrayList<String>();
        constraints.add("equal");
        constraints.add("not equal");
        if(var.getDataType() == "number") {
            constraints.add("greater");
            constraints.add("lesser");
        }a
        return constraints;
    }
}
