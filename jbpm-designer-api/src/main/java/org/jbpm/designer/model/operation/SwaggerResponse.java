package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class SwaggerResponse {
    String description;
    SwaggerSchema schema;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwaggerSchema getSchema() {
        return schema;
    }

    public void setSchema(SwaggerSchema schema) {
        this.schema = schema;
    }
}
