package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.ParameterMapping;

import javax.inject.Inject;
import java.util.List;

@Templated("ParametersDetail.html#widget")
public class ParametersDetail extends Composite {
    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<ParameterMapping, ParameterDetail> parameters;

    public void setAcceptableVariables(List<Variable> acceptableVariables) {
        for(int row = 0; row < parameters.getWidgetCount(); row++) {
            ParameterDetail widget = parameters.getWidget(row);
            widget.setAcceptableVariables(acceptableVariables);
        }
    }

    public ListWidget getListWidget() {
        return parameters;
    }
}
