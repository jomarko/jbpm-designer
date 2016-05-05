package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.designer.model.Variable;

@Portable
@Bindable
public class ParameterMapping {
    private SwaggerParameter parameter;

    private boolean required;

    private Variable variable;

    public SwaggerParameter getParameter() {
        return parameter;
    }

    public void setParameter(SwaggerParameter parameter) {
        this.parameter = parameter;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }
}
