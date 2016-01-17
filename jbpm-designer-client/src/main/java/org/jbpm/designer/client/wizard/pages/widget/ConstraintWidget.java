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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.shared.Constraint;
import org.jbpm.designer.client.shared.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstraintWidget extends Composite implements HasModel<Constraint>, HasValue<Constraint> {

    private DataBinder<Constraint> binder = DataBinder.forType(Constraint.class);

    private ValueListBox<Variable> variable = new ValueListBox<Variable>(new Renderer<Variable>() {
        @Override
        public String render(Variable variable) {
            if(variable != null && variable.getName() != null) {
                return variable.getName();
            } else {
                return "";
            }
        }

        @Override
        public void render(Variable variable, Appendable appendable) throws IOException {
            String s = render(variable);
            appendable.append(s);
        }
    });

    private ValueListBox<String> constraint = new ValueListBox<String>(new Renderer<String>() {
        @Override
        public String render(String s) {
            return s;
        }

        @Override
        public void render(String s, Appendable appendable) throws IOException {
            appendable.append(s);
        }
    });

    private TextBox constraintValue = new TextBox();

    public ConstraintWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(variable);
        panel.add(constraint);
        panel.add(constraintValue);
        initWidget(panel);

        binder.bind(variable, "variable").bind(constraint, "constraint").bind(constraintValue, "constraintValue").getModel();
        variable.addValueChangeHandler(new ValueChangeHandler<Variable>() {
            @Override
            public void onValueChange(ValueChangeEvent<Variable> valueChangeEvent) {
                constraint.setValue("");
                constraint.setAcceptableValues(getConstraints(valueChangeEvent.getValue()));
            }
        });
    }

    @Override
    public Constraint getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(Constraint constraint) {
        binder.setModel(constraint);
    }

    @Override
    public Constraint getValue() {
        return getModel();
    }

    @Override
    public void setValue(Constraint constraint) {
        setValue(constraint, false);
    }

    @Override
    public void setValue(Constraint constraint, boolean fireEvents) {
        setModel(constraint);
        if(fireEvents) {
            ValueChangeEvent.fire(this, constraint);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Constraint> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void setVariables(List<Variable> variables) {
        variable.setAcceptableValues(variables);
    }

    private List<String> getConstraints(Variable var) {
        List<String> constraints = new ArrayList<String>();
        constraints.add("equal");
        constraints.add("not equal");
        if(var.getDataType() == "number") {
            constraints.add("greater");
            constraints.add("lesser");
        }
        return constraints;
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        binder.addPropertyChangeHandler(handler);
    }
}
