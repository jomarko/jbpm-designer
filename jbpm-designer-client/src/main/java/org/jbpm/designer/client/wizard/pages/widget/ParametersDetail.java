package org.jbpm.designer.client.wizard.pages.widget;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.model.operation.ParameterMapping;
import org.jbpm.designer.model.operation.SwaggerParameter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Templated("ParametersDetail.html#widget")
public class ParametersDetail extends Composite {
    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<ParameterMapping, ParameterDetail> parameters;

    public void setAcceptableVariables(Map<SwaggerParameter,List<Variable>> acceptableVariables) {
        for(int row = 0; row < parameters.getWidgetCount(); row++) {
            ParameterDetail widget = parameters.getWidget(row);
            if(widget != null && widget.getModel() != null && widget.getModel().getParameter() != null) {
                if(acceptableVariables.containsKey(widget.getModel().getParameter())) {
                    widget.setAcceptableVariables(acceptableVariables.get(widget.getModel().getParameter()));
                } else {
                    widget.setAcceptableVariables(new ArrayList<Variable>());
                }
            }
        }
    }

    public ListWidget getListWidget() {
        return parameters;
    }

    public void setRequiredParametersHelpVisibility(boolean visibility) {
        for(ParameterMapping parameterMapping : parameters.getValue()) {
            ParameterDetail widget = parameters.getWidget(parameterMapping);
            if(visibility && parameterMapping.isRequired()) {
                widget.showNameAsRequired(true);
            } else {
                widget.showNameAsRequired(false);
            }
        }
    }
}
