package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.Map;

@Portable
@Bindable
public class SwaggerDefinition {
    private Map<String, SwaggerPath> paths;

    public Map<String, SwaggerPath> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, SwaggerPath> paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        return "SwaggerDefinition{" +
                "paths=" + paths +
                '}';
    }
}
