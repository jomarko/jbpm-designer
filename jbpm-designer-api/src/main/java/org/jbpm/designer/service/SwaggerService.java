package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.designer.model.operation.Swagger;
import org.uberfire.backend.vfs.Path;

import java.io.IOException;
import java.util.List;

@Remote
public interface SwaggerService {

    Swagger getSwagger(Path path) throws IOException;

    Swagger createSwagger(String context, String fileName, final String swaggerContent );
}
