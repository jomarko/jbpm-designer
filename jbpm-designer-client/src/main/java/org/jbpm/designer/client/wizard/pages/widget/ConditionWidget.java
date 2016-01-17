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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.shared.Condition;
import org.jbpm.designer.client.shared.Variable;

import javax.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class ConditionWidget extends Composite implements HasModel<Condition>, HasValue<Condition>, HasValueChangeHandlers<Condition> {

    DataBinder<Condition> binder = DataBinder.forType(Condition.class);

    private ConstraintWidget constraint = new ConstraintWidget();

    private CheckBox constraintSatisfied = new CheckBox("satisfied");

    public ConditionWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(constraint);
        panel.add(constraintSatisfied);
        initWidget(panel);

        binder.bind(constraint, "constraint").getModel();

        constraintSatisfied.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                Condition model = getModel();
                int positive = model.getPositiveTaskId();
                int negative = model.getNegativeTaskId();
                model.setPositiveTaskId(negative);
                model.setNegativeTaskId(positive);
                setModel(model);
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
        constraint.setVariables(variables);
    }

    public void unbind() {
        binder.unbind();
    }

    public void rebind() {
        binder.bind(constraint, "constraint");
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        binder.addPropertyChangeHandler(handler);
        constraint.setPropertyChangeHandler(handler);
    }

    public void setConstraintSatisfied(boolean value) {
        constraintSatisfied.setValue(value, false);
    }
}
