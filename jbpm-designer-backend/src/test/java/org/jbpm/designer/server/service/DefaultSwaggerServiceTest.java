package org.jbpm.designer.server.service;

import org.jbpm.designer.model.operation.ServiceUploadResultEntry;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSwaggerServiceTest {

    private static String getTestFileContent(String filename) throws URISyntaxException, IOException {
        URL fileURL = DefaultSwaggerServiceTest.class.getResource(filename);
        File file = new File(fileURL.getFile());
        return new String(Files.readAllBytes(Paths.get(file.getPath())));
    }

    @Mock
    private Path path;

    @Mock
    private Asset<String> asset;

    @Mock
    private Repository repository;

    @Spy
    @InjectMocks
    private DefaultSwaggerService swaggerService = new DefaultSwaggerService();

    @Before
    public void setUp() throws Exception {
        when(repository.loadAssetFromPath(path)).thenReturn(asset);
    }

    @Test
    public void testConstructCorrectSwagger() throws Exception {
        ServiceUploadResultEntry result = swaggerService.constructSwagger(getTestFileContent("vacations.swagger"), "vacations.swagger").getResultEntry();
        assertEquals("ok", result.getStatus());
        assertEquals("vacations.swagger", result.getFileName());
        assertEquals("1.0.0", result.getVersion());
        assertEquals("vacations", result.getApiName());
    }

    @Test
    public void testConstructCorruptedSwagger() throws Exception {
        ServiceUploadResultEntry result = swaggerService.constructSwagger(getTestFileContent("vacations-corrupted.swagger"), "vacations.swagger").getResultEntry();
        assertEquals("error", result.getStatus());
        assertTrue(!result.getMessage().isEmpty());
    }

    @Test
    public void testGetCorrectSwagger() throws Exception {
        when(path.getFileName()).thenReturn("vacations.swagger");
        when(asset.getAssetContent()).thenReturn(getTestFileContent("vacations.swagger"));
        assertNotNull(swaggerService.getSwagger(path));
    }

    @Test
    public void testGetCorruptedSwagger() throws Exception {
        when(path.getFileName()).thenReturn("vacations-corrupted.swagger");
        when(asset.getAssetContent()).thenReturn(getTestFileContent("vacations-corrupted.swagger"));
        assertNull(swaggerService.getSwagger(path));
    }
}
