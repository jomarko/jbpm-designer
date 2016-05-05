package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.model.StandardEvent;


import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ProcessStartEventPageViewImpl extends Composite implements ProcessStartEventPageView {

    interface ProcessStartEventPageViewImplBinder
            extends
            UiBinder<Widget, ProcessStartEventPageViewImpl> {
    }

    private static ProcessStartEventPageViewImplBinder uiBinder = GWT.create(ProcessStartEventPageViewImplBinder.class);

    private Presenter presenter;

    private StandardEvent standardStartEvent = new StandardEvent();

    @Inject
    public ProcessStartEventPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    RadioButton standard;

    @UiField
    RadioButton timer;

    @UiField
    RadioButton signal;

    @Inject
    private TimerWidget timerDetails;

    @Inject
    private SignalWidget signalDetails;

    @UiField
    VerticalPanel container;

    @UiHandler("standard")
    void standardClicked(ClickEvent event) {
        timerDetails.setVisible(false);
        signalDetails.setVisible(false);
        presenter.firePageChangedEvent();
    }

    @UiHandler("timer")
    void timerClicked(ClickEvent event) {
        timerDetails.setVisible(true);
        signalDetails.setVisible(false);
        presenter.firePageChangedEvent();
    }

    @UiHandler("signal")
    void signalClicked(ClickEvent event) {
        timerDetails.setVisible(false);
        signalDetails.setVisible(true);
        presenter.firePageChangedEvent();
    }

    @PostConstruct
    public void initializeView() {
        container.add(timerDetails);
        container.add(signalDetails);
        timerDetails.setVisible(false);
        signalDetails.setVisible(false);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        timerDetails.setPresenter(presenter);
        signalDetails.setPresenter(presenter);
    }

    @Override
    public boolean isSelectedSignalStart() {
        return signal.getValue();
    }

    @Override
    public String getDefinedSignal() {
        return signalDetails.getSignal();
    }

    @Override
    public boolean isSelectedDateStart() {
        return timer.getValue() && timerDetails.isDateSelected();
    }

    @Override
    public boolean isSelectedDelayStart() {
        return timer.getValue() && timerDetails.isDelaySelected();
    }

    @Override
    public boolean isSelectedCycleStart() {
        return timer.getValue() && timerDetails.isCycleSelected();
    }

    @Override
    public String getDefinedTimeValue() {
        return timerDetails.getTimerValue();
    }

    @Override
    public StandardEvent getDefinedEvent() {
        if(timer.getValue()) {
            return timerDetails.getModel();
        }
        if(signal.getValue()) {
            return signalDetails.getModel();
        }
        return standardStartEvent;
    }
}
