package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;


import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;

@Dependent
public class ProcessStartEventPageViewImpl extends Composite implements ProcessStartEventPageView {

    interface ProcessStartEventPageViewImplBinder
            extends
            UiBinder<Widget, ProcessStartEventPageViewImpl> {
    }

    private static ProcessStartEventPageViewImplBinder uiBinder = GWT.create(ProcessStartEventPageViewImplBinder.class);


    @Inject
    public ProcessStartEventPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        ArrayList<String> signals = new ArrayList<String>();
        signals.add("abc");
        signals.add("efg");
        signalDetails.setSignals(signals);
    }

    @UiField
    RadioButton standard;

    @UiField
    RadioButton timer;

    @UiField
    RadioButton signal;

    @UiField
    TimerWidget timerDetails;

    @UiField
    SignalWidget signalDetails;

    @UiHandler("standard")
    void standardClicked(ClickEvent event) {
        timerDetails.setVisible(false);
        signalDetails.setVisible(false);
    }

    @UiHandler("timer")
    void timerClicked(ClickEvent event) {
        timerDetails.setVisible(true);
        signalDetails.setVisible(false);
    }

    @UiHandler("signal")
    void signalClicked(ClickEvent event) {
        timerDetails.setVisible(false);
        signalDetails.setVisible(true);
    }

}
