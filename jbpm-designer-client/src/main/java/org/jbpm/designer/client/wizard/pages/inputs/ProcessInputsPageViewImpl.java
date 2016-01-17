package org.jbpm.designer.client.wizard.pages.inputs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.shared.Variable;
import org.jbpm.designer.client.wizard.pages.widget.DeletableFlexTable;
import org.jbpm.designer.client.wizard.pages.widget.InputRow;
import org.jbpm.designer.client.wizard.pages.widget.InputsTable;
import org.jbpm.designer.client.wizard.pages.widget.WidgetWithModel;

import javax.enterprise.context.Dependent;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class ProcessInputsPageViewImpl extends Composite implements ProcessInputsPageView,
                                                                    DeletableFlexTable.RowsHandler<Variable> {

    interface ProcessInputsPageViewImplBinder
            extends
            UiBinder<Widget, ProcessInputsPageViewImpl> {
    }

    private static ProcessInputsPageViewImplBinder uiBinder = GWT.create(ProcessInputsPageViewImplBinder.class);

    private Map<InputRow, Variable> inputsMapper = new HashMap<InputRow, Variable>();

    public ProcessInputsPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        inputs.registerRowsHandler(this);
    }

    @UiField
    InputsTable inputs;

    @Override
    public void addedRow(WidgetWithModel<Variable> widget) {
        inputsMapper.put((InputRow) widget, widget.getModel());
    }

    @Override
    public void rowSelected(WidgetWithModel<Variable> widget) {

    }

    @Override
    public void rowDeleted(WidgetWithModel<Variable> widget) {
        inputsMapper.remove(widget);
    }
}
