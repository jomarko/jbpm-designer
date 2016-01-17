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

import javax.enterprise.context.Dependent;

@Dependent
public class SignalWidget extends Composite {

    interface SignalWidgetBinder
            extends
            UiBinder<Widget, SignalWidget> {
    }

    private static SignalWidgetBinder uiBinder = GWT.create(SignalWidgetBinder.class);

    private ProcessStartEventPageView.Presenter presenter;

    @UiField
    TextBox signal;

    @UiField
    HelpBlock signalHelp;

    public SignalWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        signal.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                presenter.firePageChangedEvent();
            }
        });
    }

    public String getSignal() {
        return signal.getText();
    }

    public void setPresenter(ProcessStartEventPageView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void showHelp() {
        signalHelp.setVisible(true);
    }

    public void hideHelp() {
        signalHelp.setVisible(false);
    }
}
