package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.Map;

@Portable
@Bindable
public class Swagger {

    private String urlBase;

    private Map<String, SwaggerPath> paths;

    private Map<String, SwaggerDefinition> definitions;

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    public Map<String, SwaggerPath> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, SwaggerPath> paths) {
        this.paths = paths;
    }

    public Map<String, SwaggerDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, SwaggerDefinition> definitions) {
        this.definitions = definitions;
    }
}
