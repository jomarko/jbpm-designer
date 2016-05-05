package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.designer.model.operation.Swagger;

import java.io.IOException;

@Remote
public interface SwaggerService {
    Swagger getSwagger() throws IOException;
}
