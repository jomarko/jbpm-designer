package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.TimerEvent;

public class TimerWidget extends Composite implements HasModel<TimerEvent> {

    private DataBinder<TimerEvent> dataBinder = DataBinder.forType(TimerEvent.class);

    interface TimerWidgetBinder
            extends
            UiBinder<Widget, TimerWidget> {
    }

    private static TimerWidgetBinder uiBinder = GWT.create(TimerWidgetBinder.class);

    private ProcessStartEventPageView.Presenter presenter;

    @UiField
    RadioButton date;

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

        dataBinder.bind(timerValue, "timerExpression").getModel();
        date.addValueChangeHandler(getHandler());
        delay.addValueChangeHandler(getHandler());
        cycle.addValueChangeHandler(getHandler());
        timerValue.addValueChangeHandler(getHandler());

    }

    @UiHandler("date")
    void dateClicked(ClickEvent event) {
        TimerEvent startEvent = dataBinder.getModel();
        startEvent.setTimerType(TimerEvent.DATE);
        dataBinder.setModel(startEvent);
    }

    @UiHandler("delay")
    void delayClicked(ClickEvent event) {
        TimerEvent startEvent = dataBinder.getModel();
        startEvent.setTimerType(TimerEvent.DURATION);
        dataBinder.setModel(startEvent);
    }

    @UiHandler("cycle")
    void cycleClicked(ClickEvent event) {
        TimerEvent startEvent = dataBinder.getModel();
        startEvent.setTimerType(TimerEvent.CYCLE);
        dataBinder.setModel(startEvent);
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

    @Override
    public TimerEvent getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(TimerEvent timerEvent) {
        dataBinder.setModel(timerEvent);
    }
}
