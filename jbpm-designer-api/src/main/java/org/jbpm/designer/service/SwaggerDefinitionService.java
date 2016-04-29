package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.designer.model.operation.SwaggerDefinition;

import java.io.IOException;

@Remote
public interface SwaggerDefinitionService {
    SwaggerDefinition getDefinition() throws IOException;
}
