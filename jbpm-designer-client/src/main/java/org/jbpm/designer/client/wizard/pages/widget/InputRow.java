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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Variable;

public class InputRow extends Composite implements HasModel<Variable>, HasValue<Variable> {

    DataBinder<Variable> dataBinder = DataBinder.forType(Variable.class);

    private TextBox name;
    private ValueListBox<String> dataType;

    public InputRow() {
        name = new TextBox();
        dataType = new ValueListBox<String>(new ToStringRenderer());
        dataType.setValue("boolean", true);
        dataType.setValue("number", true);
        dataType.setValue("string", true);

        HorizontalPanel row =  new HorizontalPanel();
        row.add(name);
        row.add(dataType);
        initWidget(row);

        dataBinder.bind(name, "name").bind(dataType, "dataType").getModel();

        setStyleName("cellWithMargin");
    }

    @Override
    public Variable getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Variable input) {
        dataBinder.setModel(input);
    }

    @Override
    public Variable getValue() {
        return getModel();
    }

    @Override
    public void setValue(Variable variable) {
        setValue(variable, false);
    }

    @Override
    public void setValue(Variable variable, boolean fireEvents) {
        setModel(variable);
        if(fireEvents) {
            ValueChangeEvent.fire(this, variable);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Variable> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void setPropertyChangeHandler(PropertyChangeHandler handler) {
        dataBinder.addPropertyChangeHandler(handler);
    }
}
