package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import java.util.Map;

@Portable
@Bindable
public class Swagger {

    private String host;

    private String basePath;

    private SwaggerInfo info;

    private Map<String, SwaggerPath> paths;

    private Map<String, SwaggerDefinition> definitions;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public SwaggerInfo getInfo() {
        return info;
    }

    public void setInfo(SwaggerInfo info) {
        this.info = info;
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
