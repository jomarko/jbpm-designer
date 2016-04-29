package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;

@Portable
@Bindable
public class SwaggerOperation {
    private String description;
    private String operationId;
    private List<SwaggerParameter> parameters;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<SwaggerParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SwaggerParameter> parameters) {
        this.parameters = parameters;
    }
}
