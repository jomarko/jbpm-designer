package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.model.SignalEvent;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class SignalWidget extends Composite implements HasModel<SignalEvent> {

    private DataBinder<SignalEvent> dataBinder;

    interface SignalWidgetBinder
            extends
            UiBinder<Widget, SignalWidget> {
    }

    private static SignalWidgetBinder uiBinder = GWT.create(SignalWidgetBinder.class);

    protected ProcessStartEventPageView.Presenter presenter;

    @Inject
    Event<NotificationEvent> notification;

    @UiField
    TextBox signal;

    @UiField
    HelpBlock signalHelp;

    public SignalWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    public void initDataBinder() {
        dataBinder = DataBinder.forType(SignalEvent.class);
        dataBinder.bind(signal, "signalName").getModel();
    }

    @PostConstruct
    public void initChangeHandler() {
        signal.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                presenter.firePageChangedEvent();
                if(!presenter.isStartValid()) {
                    notification.fire( new NotificationEvent(
                            DesignerEditorConstants.INSTANCE.signalFormat(),
                            NotificationEvent.NotificationType.ERROR));
                }
            }
        });
    }

    public String getSignal() {
        return signal.getText();
    }

    public void setPresenter(ProcessStartEventPageView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public SignalEvent getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(SignalEvent signalEvent) {
        dataBinder.setModel(signalEvent);
    }
}
