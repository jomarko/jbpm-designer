package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;

public class TimerWidget extends Composite {

    interface TimerWidgetBinder
            extends
            UiBinder<Widget, TimerWidget> {
    }

    private static TimerWidgetBinder uiBinder = GWT.create(TimerWidgetBinder.class);

    private ProcessStartEventPageView.Presenter presenter;

    @UiField
    RadioButton delay;

    @UiField
    RadioButton cycle;

    @UiField
    TextBox timerValue;

    @UiField
    HelpBlock timerHelp;

    public TimerWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        delay.addValueChangeHandler(getHandler());
        cycle.addValueChangeHandler(getHandler());
        timerValue.addValueChangeHandler(getHandler());

    }

    public boolean isDelaySelected() {
        return delay.getValue();
    }

    public boolean isCycleSelected() {
        return cycle.getValue();
    }

    public String getTimerValue() {
        return timerValue.getText();
    }

    public void setPresenter(ProcessStartEventPageView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void showHelp() {
        timerHelp.setVisible(true);
    }

    public void hideHelp() {
        timerHelp.setVisible(false);
    }

    private ValueChangeHandler getHandler() {
        return new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent valueChangeEvent) {
                presenter.firePageChangedEvent();
            }
        };
    }
}
