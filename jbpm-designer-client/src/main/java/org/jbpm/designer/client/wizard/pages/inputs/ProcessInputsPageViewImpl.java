package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.jbpm.designer.model.Variable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class ProcessInputsPageViewImpl extends Composite implements ProcessInputsPageView{

    interface ProcessInputsPageViewImplBinder
            extends
            UiBinder<Widget, ProcessInputsPageViewImpl> {
    }

    private static ProcessInputsPageViewImplBinder uiBinder = GWT.create(ProcessInputsPageViewImplBinder.class);

    public ProcessInputsPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private List<String> dataTypes = new ArrayList<String>();

    private Presenter presenter;

    @UiHandler("addButton")
    public void addButtonHandler(ClickEvent event) {
        inputs.addVariable(presenter.getDefaultModel(), dataTypes);
        presenter.firePageChangedEvent();
    }

    @UiField
    VerticalPanel inputsPanel;

    @Inject
    private ProcessInputsTable inputs;

    @UiField
    HelpBlock variablesHelp;

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        inputsPanel.add(inputs);
        inputs.addValueChangeHandler(new ValueChangeHandler<List<Variable>>() {
            @Override
            public void onValueChange(ValueChangeEvent<List<Variable>> valueChangeEvent) {
                presenter.firePageChangedEvent();
            }
        });
        dataTypes.add("String");
        dataTypes.add("Float");
        dataTypes.add("Boolean");
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
}
