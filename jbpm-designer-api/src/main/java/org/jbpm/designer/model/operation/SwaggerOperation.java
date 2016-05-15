package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;
import java.util.Map;

@Portable
@Bindable
public class SwaggerOperation {
    private String description;
    private String operationId;
    private List<SwaggerParameter> parameters;
    private Map<String, SwaggerResponse> responses;

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

    public Map<String, SwaggerResponse> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, SwaggerResponse> responses) {
        this.responses = responses;
    }
}
