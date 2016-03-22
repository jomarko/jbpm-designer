package org.jbpm.designer.client.wizard.pages.start;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.model.StandardEvent;


public interface ProcessStartEventPageView extends IsWidget {
    interface Presenter {
        boolean isStartValid();

        void firePageChangedEvent();
    }

    void init(Presenter presenter);

    boolean isSelectedNormalStart();

    boolean isSelectedSignalStart();

    boolean isSelectedDelayStart();

    boolean isSelectedCycleStart();

    String getDefinedSignal();

    String getDefinedTimeValue();

    void showSignalError();

    void hideSignalError();

    void showTimeError();

    void hideTimeError();

    StandardEvent getDefinedEvent();
}
