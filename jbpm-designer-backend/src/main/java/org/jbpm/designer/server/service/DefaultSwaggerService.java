package org.jbpm.designer.server.service;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.model.operation.Swagger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.service.SwaggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.java.nio.file.api.FileSystemProviders;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import java.util.List;

@Service
@ApplicationScoped
public class DefaultSwaggerService implements SwaggerService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSwaggerService.class);

    @Inject
    private Repository repository;

    @Inject
    private IOSearchService ioSearchService;

    private List<Swagger> swaggers = new ArrayList<Swagger>();

    @Override
    public Swagger getSwagger(Path path) throws IOException {
        Asset<String> asset = repository.loadAssetFromPath(path);
        if(asset != null) {
            return constructSwagger(asset.getAssetContent());
        }
        return null;
    }

    @Override
    public Swagger createSwagger(String context, String fileName, final String swaggerContent ) {
        final Path path = Paths.convert( FileSystemProviders.getDefaultProvider().getPath( URI.create(context + "/" + fileName) ) );
        String location = Paths.convert( path ).getParent().toString();
        String name = path.getFileName();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder( name );
        builder.location( location ).content( swaggerContent ).uniqueId(path.toURI());
        Asset<String> swaggerAsset = builder.getAsset();
        repository.createAsset( swaggerAsset );
        Swagger swagger = constructSwagger(swaggerAsset.getAssetContent());
        if(swagger != null) {
            swaggers.add(swagger);
            return swagger;
        }

        return null;
    }

    private Swagger constructSwagger(String swaggerJson) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Swagger swagger = null;
        try {
            swagger = mapper.readValue(swaggerJson, Swagger.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return swagger;
    }
}
