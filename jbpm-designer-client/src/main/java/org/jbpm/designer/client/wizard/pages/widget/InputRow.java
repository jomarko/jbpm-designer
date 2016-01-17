package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jbpm.designer.client.shared.Variable;

import java.io.IOException;

public class InputRow extends Composite implements WidgetWithModel<Variable>, HasValue<Variable> {

    DataBinder<Variable> dataBinder = DataBinder.forType(Variable.class);

    private TextBox name;
    private ValueListBox<String> dataType;

    public InputRow() {
        name = new TextBox();
        dataType = new ValueListBox<String>(new Renderer<String>() {
            @Override
            public String render(String object) {
                String s = "";
                if (object != null) {
                    s = object.toString();
                }
                return s;
            }

            @Override
            public void render(String object, Appendable appendable) throws IOException {
                String s = render(object);
                appendable.append(s);
            }
        });
        dataType.setValue("boolean", true);
        dataType.setValue("number", true);
        dataType.setValue("string", true);

        HorizontalPanel row =  new HorizontalPanel();
        row.add(name);
        row.add(dataType);
        initWidget(row);

        dataBinder.bind(name, "name").bind(dataType, "dataType");
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
        return dataBinder.getModel();
    }

    @Override
    public void setValue(Variable variable) {
        dataBinder.setModel(variable);
    }

    @Override
    public void setValue(Variable variable, boolean throwEvent) {
        setValue(variable);
        if(throwEvent) {
            ValueChangeEvent.fire(this, variable);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Variable> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }
}
