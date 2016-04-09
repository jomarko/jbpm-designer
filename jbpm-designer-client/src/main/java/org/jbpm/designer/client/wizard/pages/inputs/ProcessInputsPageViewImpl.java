package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.client.wizard.pages.widget.DeletableFlexTable;
import org.jbpm.designer.client.wizard.pages.widget.InputRow;
import org.jbpm.designer.client.wizard.pages.widget.InputsTable;

import javax.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class ProcessInputsPageViewImpl extends Composite implements ProcessInputsPageView,
                                                                    DeletableFlexTable.RowsHandler {

    interface ProcessInputsPageViewImplBinder
            extends
            UiBinder<Widget, ProcessInputsPageViewImpl> {
    }

    private static ProcessInputsPageViewImplBinder uiBinder = GWT.create(ProcessInputsPageViewImplBinder.class);

    public ProcessInputsPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        inputs.registerRowsHandler(this);
    }

    private PropertyChangeHandler handler = new PropertyChangeHandler() {
        @Override
        public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
            presenter.firePageChangedEvent();
        }
    };

    private Presenter presenter;

    @UiHandler("addButton")
    public void addButtonHandler(ClickEvent event) {
        inputs.addNewRow();
    }

    @UiField
    InputsTable inputs;

    @Override
    public void addedRow(Widget widget) {
        ((InputRow) widget).setModel(presenter.getDefaultModel());
        ((InputRow) widget).setPropertyChangeHandler(handler);
        presenter.firePageChangedEvent();
    }

    @Override
    public void rowDeleted() {
        presenter.firePageChangedEvent();
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        inputs.clear();
    }

    @Override
    public List<Variable> getInputs() {
        return inputs.getModels();
    }

    @Override
    public void showAsValid(int row) {
        inputs.setNormalRowColor(row, 0);
    }

    @Override
    public void showAsInvalid(int row) {
        inputs.setRedRowColor(row, 0);
    }
}
