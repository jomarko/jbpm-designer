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
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.model.TimerEvent;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

public class TimerWidget extends Composite implements HasModel<TimerEvent> {

    private DataBinder<TimerEvent> dataBinder;

    interface TimerWidgetBinder
            extends
            UiBinder<Widget, TimerWidget> {
    }

    private static TimerWidgetBinder uiBinder = GWT.create(TimerWidgetBinder.class);

    protected ProcessStartEventPageView.Presenter presenter;

    @Inject
    Event<NotificationEvent> notification;

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
        timerHelp.setText(DesignerEditorConstants.INSTANCE.timerDelayHelp());
    }

    @PostConstruct
    public void initDataBinder() {
        dataBinder = DataBinder.forType(TimerEvent.class);
        dataBinder.bind(timerValue, "timerExpression").getModel();
    }

    @PostConstruct
    public void initValueChangeHandlers() {
        date.addValueChangeHandler(getHandler());
        delay.addValueChangeHandler(getHandler());
        cycle.addValueChangeHandler(getHandler());
        timerValue.addValueChangeHandler(getHandler());
    }

    @UiHandler("date")
    void dateClicked(ClickEvent event) {
        TimerEvent startEvent = dataBinder.getModel();
        startEvent.setTimerType(TimerEvent.DATE);
        timerHelp.setText(DesignerEditorConstants.INSTANCE.timerDateHelp());
        dataBinder.setModel(startEvent);
    }

    @UiHandler("delay")
    void delayClicked(ClickEvent event) {
        TimerEvent startEvent = dataBinder.getModel();
        startEvent.setTimerType(TimerEvent.DURATION);
        timerHelp.setText(DesignerEditorConstants.INSTANCE.timerDelayHelp());
        dataBinder.setModel(startEvent);
    }

    @UiHandler("cycle")
    void cycleClicked(ClickEvent event) {
        TimerEvent startEvent = dataBinder.getModel();
        startEvent.setTimerType(TimerEvent.CYCLE);
        timerHelp.setText(DesignerEditorConstants.INSTANCE.timerCycleHelp());
        dataBinder.setModel(startEvent);
    }

    public boolean isDateSelected() {
        return date.getValue();
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

    private ValueChangeHandler getHandler() {
        return new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent valueChangeEvent) {
                presenter.firePageChangedEvent();
                if(!presenter.isStartValid()) {
                    if(isDelaySelected() || isCycleSelected()) {
                        notification.fire( new NotificationEvent(
                                DesignerEditorConstants.INSTANCE.timerCycleAndDelayFormat(),
                                NotificationEvent.NotificationType.ERROR));
                    }
                    if(isDateSelected()) {
                        notification.fire( new NotificationEvent(
                                DesignerEditorConstants.INSTANCE.timerDateFormat(),
                                NotificationEvent.NotificationType.ERROR));
                    }
                }
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
