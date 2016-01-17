package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;

import javax.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class SignalWidget extends Composite {

    private ListBox existingSignals;

    private TextBox newSignal;

    private Button newSignalBtn;

    public SignalWidget() {
        VerticalPanel panel = new VerticalPanel();

        Label existingSignalsLabel = new Label("existing signals");
        panel.add(existingSignalsLabel);

        existingSignals = new ListBox();
        panel.add(existingSignals);

        Label newSignalLabel = new Label("new signal");
        panel.add(newSignalLabel);

        newSignal = new TextBox();
        panel.add(newSignal);

        newSignalBtn = new Button("add");
        newSignalBtn.addClickHandler(newSignalHandler());
        panel.add(newSignalBtn);


        initWidget(panel);
    }

    public void setSignals(List<String> signals) {
        existingSignals.clear();
        for(String signal : signals) {
            existingSignals.addItem(signal);
        }
    }

    private ClickHandler newSignalHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                existingSignals.addItem(newSignal.getText());
                existingSignals.setSelectedIndex(existingSignals.getItemCount() - 1);
                newSignal.clear();
            }
        };
    }
}
