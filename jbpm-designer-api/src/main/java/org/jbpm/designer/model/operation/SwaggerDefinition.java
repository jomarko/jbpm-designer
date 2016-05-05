package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.List;
import java.util.Map;

@Bindable
@Portable
public class SwaggerDefinition {
    private List<String> required;
    private Map<String, SwaggerProperty> properties;

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public Map<String, SwaggerProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SwaggerProperty> properties) {
        this.properties = properties;
    }
}
