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
import org.gwtbootstrap3.client.ui.*;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Condition;
import org.jbpm.designer.model.Constraint;
import org.jbpm.designer.model.Variable;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class ConditionWidget extends Composite implements HasModel<Condition>, HasValue<Condition> {

    private static final String CONSTRAINT_VARIABLE = "constraint.variable";
    private static final String CONSTRAINT_CONSTRAINT = "constraint.constraint";
    private static final String CONSTRAINT_CONSTRAINT_VALUE = "constraint.constraintValue";
    private static final String EXECUTE_IF_CONSTRAINT_SATISFIED = "executeIfConstraintSatisfied";

    interface ConditionWidgetBinder
            extends
            UiBinder<Widget, ConditionWidget> {
    }

    private static ConditionWidgetBinder uiBinder = GWT.create(ConditionWidgetBinder.class);

    DataBinder<Condition> binder = DataBinder.forType(Condition.class);

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
    FormLabel constraintValueLabel;

    @UiField
    CheckBox constraintSatisfied;

    public ConditionWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        bindDataBinder();

        variable.addValueChangeHandler(new ValueChangeHandler<Variable>() {
            @Override
            public void onValueChange(ValueChangeEvent<Variable> valueChangeEvent) {
                constraint.setValue("");
                constraint.setAcceptableValues(getConstraints(valueChangeEvent.getValue()));
                if(valueChangeEvent.getValue().getDataType().compareTo("Boolean") == 0) {
                    constraintValueLabel.setVisible(false);
                    constraintValue.setVisible(false);
                    constraintValueHelp.setVisible(false);
                } else {
                    constraintValueLabel.setVisible(true);
                    constraintValue.setVisible(true);
                    constraintValueHelp.setVisible(true);
                }
            }
        });
    }

    @Override
    public Condition getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(Condition condition) {
        binder.setModel(condition);
    }

    @Override
    public Condition getValue() {
        return getModel();
    }

    @Override
    public void setValue(Condition condition) {
        setValue(condition, false);
    }

    @Override
    public void setValue(Condition condition, boolean fireEvents) {
        setModel(condition);
        if(fireEvents) {
            ValueChangeEvent.fire(this, condition);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Condition> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void setVariables(List<Variable> variables) {
        variable.setAcceptableValues(variables);
    }

    public void unbind() {
        binder.unbind();
    }

    public void rebind() {
        bindDataBinder();
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        binder.addPropertyChangeHandler(CONSTRAINT_VARIABLE, handler);
        binder.addPropertyChangeHandler(CONSTRAINT_CONSTRAINT, handler);
        binder.addPropertyChangeHandler(CONSTRAINT_CONSTRAINT_VALUE, handler);
        binder.addPropertyChangeHandler(EXECUTE_IF_CONSTRAINT_SATISFIED, handler);

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
        if(var.getDataType().compareTo("Float") == 0
            || var.getDataType().compareTo("Integer") == 0
            || var.getDataType().compareTo("Double") == 0) {
            constraints.add(Constraint.LESS_THAN);
            constraints.add(Constraint.EQUAL_OR_LESS_THAN);
            constraints.add(Constraint.GREATER_THAN);
            constraints.add(Constraint.EQUAL_OR_GREATER_THAN);
            constraints.add(Constraint.EQUAL_TO);
        }
        if(var.getDataType().compareTo("String") == 0) {
            constraints.add(Constraint.CONTAINS);
            constraints.add(Constraint.STARTS_WITH);
            constraints.add(Constraint.EQUAL_TO);
        }
        if(var.getDataType().compareTo("Boolean") == 0) {
            constraints.add(Constraint.IS_TRUE);
            constraints.add(Constraint.IS_FALSE);
        }
        return constraints;
    }

    private void bindDataBinder() {
        binder.bind(variable, CONSTRAINT_VARIABLE)
                .bind(constraint, CONSTRAINT_CONSTRAINT)
                .bind(constraintValue, CONSTRAINT_CONSTRAINT_VALUE)
                .bind(constraintSatisfied, EXECUTE_IF_CONSTRAINT_SATISFIED)
                .getModel();
    }
}
