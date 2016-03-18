package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.model.Variable;

import java.util.List;


public interface ProcessInputsPageView extends IsWidget {

    interface Presenter {
        Variable getDefaultModel();

        boolean isVariableValid(Variable variable);

        void firePageChangedEvent();
    }

    void init(Presenter presenter);

    List<Variable> getInputs();

    void showAsValid(int row);

    void showAsInvalid(int row);
}
