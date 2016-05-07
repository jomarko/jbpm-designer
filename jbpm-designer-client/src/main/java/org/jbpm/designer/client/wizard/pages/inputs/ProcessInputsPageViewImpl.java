package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.jbpm.designer.model.Variable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class ProcessInputsPageViewImpl extends Composite implements ProcessInputsPageView {

    interface ProcessInputsPageViewImplBinder
            extends
            UiBinder<Widget, ProcessInputsPageViewImpl> {
    }

    private static ProcessInputsPageViewImplBinder uiBinder = GWT.create(ProcessInputsPageViewImplBinder.class);

    public ProcessInputsPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private Presenter presenter;

    @UiField
    VerticalPanel inputsPanel;

    @Inject
    protected ProcessInputsTable inputs;

    @UiField
    HelpBlock variablesHelp;

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        inputsPanel.add(inputs);
        inputs.addValueChangeHandler(new ValueChangeHandler<List<Variable>>() {
            @Override
            public void onValueChange(ValueChangeEvent<List<Variable>> valueChangeEvent) {
                ProcessInputsPageViewImpl.this.presenter.firePageChangedEvent();

            }
        });
        inputs.clear();
    }

    @Override
    public List<Variable> getInputs() {
        return inputs.getVariables();
    }

    @Override
    public void showAsValid(Variable variable) {
        inputs.setVariableProblemMarkVisibility(variable, false);
    }

    @Override
    public void showAsInvalid(Variable variable) {
        inputs.setVariableProblemMarkVisibility(variable, true);
    }

    @Override
    public void setVariablesHelpVisibility(boolean visibility) {
        variablesHelp.setVisible(visibility);
    }

    @Override
    public void deleteVariable(Variable variable) {
        inputs.deleteVariable(variable);
    }
}
