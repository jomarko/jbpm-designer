package org.jbpm.designer.server.service;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;
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
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.api.FileSystemProviders;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;

@Service
@ApplicationScoped
public class DefaultSwaggerService implements SwaggerService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSwaggerService.class);

    @Inject
    private Repository repository;

    @Override
    public Swagger getSwagger(Path path) throws IOException {
        Asset<String> asset = repository.loadAssetFromPath(path);
        if(asset != null) {
            return constructSwagger(asset.getAssetContent(), path.getFileName()).getSwagger();
        }
        return null;
    }

    @Override
    public ServiceUploadResultEntry createSwagger(String context, String fileName, final String swaggerContent ) {
        final Path path = Paths.convert( FileSystemProviders.getDefaultProvider().getPath( URI.create(context + "/" + fileName) ) );
        String location = Paths.convert( path ).getParent().toString();
        String name = path.getFileName();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder( name );
        builder.location( location ).content( swaggerContent ).uniqueId(path.toURI());
        Asset<String> swaggerAsset = builder.getAsset();
        try {
            repository.createAsset(swaggerAsset);
        } catch (FileAlreadyExistsException e) {
            logger.error(fileName + " already exists");
            ServiceUploadResultEntry entry = new ServiceUploadResultEntry();
            entry.setStatus("error");
            entry.setMessage(fileName + " already exists");
            return entry;
        }

        return constructSwagger(swaggerAsset.getAssetContent(), fileName).getResultEntry();
    }

    private ResultWrapper constructSwagger(String swaggerJson, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Swagger swagger = null;
        ServiceUploadResultEntry resultEntry = new ServiceUploadResultEntry();
        try {
            swagger = mapper.readValue(swaggerJson, Swagger.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            resultEntry.setStatus("error");
            resultEntry.setMessage(e.getMessage());
        }
        if(swagger != null) {
            resultEntry.setStatus("ok");
            resultEntry.setFileName(fileName);
            if (swagger.getInfo() != null && swagger.getInfo().getTitle() != null) {
                resultEntry.setApiName(swagger.getInfo().getTitle());
            }
            if (swagger.getInfo() != null && swagger.getInfo().getVersion() != null) {
                resultEntry.setVersion(swagger.getInfo().getVersion());
            }
        }
        ResultWrapper wrapper = new ResultWrapper();
        wrapper.setSwagger(swagger);
        wrapper.setResultEntry(resultEntry);
        return wrapper;
    }

    private class ResultWrapper {
        private Swagger swagger;
        private ServiceUploadResultEntry resultEntry;

        public Swagger getSwagger() {
            return swagger;
        }

        public void setSwagger(Swagger swagger) {
            this.swagger = swagger;
        }

        public ServiceUploadResultEntry getResultEntry() {
            return resultEntry;
        }

        public void setResultEntry(ServiceUploadResultEntry resultEntry) {
            this.resultEntry = resultEntry;
        }
    }
}
