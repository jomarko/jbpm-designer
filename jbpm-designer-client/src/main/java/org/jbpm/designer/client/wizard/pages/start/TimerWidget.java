package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.gwtbootstrap3.client.ui.TextBox;

public class TimerWidget extends Composite {

    public TimerWidget() {
        HorizontalPanel panel = new HorizontalPanel();

        VerticalPanel radioGroup = new VerticalPanel();
        RadioButton withDelay = new RadioButton("timerType");
        withDelay.setText("with dellay");
        radioGroup.add(withDelay);

        RadioButton repeatWithDelay = new RadioButton("timerType");
        repeatWithDelay.setText("repeat with delay");
        radioGroup.add(repeatWithDelay);

        panel.add(radioGroup);
        panel.add(new TextBox());

        initWidget(panel);

    }
}
