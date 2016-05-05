package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.model.Variable;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.List;


public interface ProcessInputsPageView extends IsWidget {

    interface Presenter {
        Variable getDefaultModel();

        boolean isVariableValid(Variable variable);

        void firePageChangedEvent();
    }

    void init(Presenter presenter);

    List<Variable> getInputs();

    void showAsValid(Variable variable);

    void showAsInvalid(Variable variable);

    void setVariablesHelpVisibility(boolean visibility);
}
