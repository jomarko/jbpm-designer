package org.jbpm.designer.server.service;

import org.jbpm.designer.server.service.DefaultDiscoverService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDiscoverServiceTest {

    @Mock
    private RefactoringQueryService queryService;

    @Spy
    @InjectMocks
    private DefaultDiscoverService discoverService = new DefaultDiscoverService();

    @Before
    public void setUp() throws Exception {
        RefactoringPageRow abc = mock(RefactoringPageRow.class);
        when(abc.getValue()).thenReturn("a.b.C");

        RefactoringPageRow cde = mock(RefactoringPageRow.class);
        when(cde.getValue()).thenReturn("c.d.E");

        when(queryService.query(anyString(), anySet(), anyBoolean())).thenReturn(Arrays.asList(abc, cde));

    }

    @Test
    public void testExistingDataTypes() throws Exception {
        List<String> dataTypes = discoverService.getExistingDataTypes();
        assertEquals(9, dataTypes.size());
        assertTrue(dataTypes.contains("a.b.C"));
        assertTrue(dataTypes.contains("c.d.E"));
        assertTrue(dataTypes.contains("Object"));
        assertTrue(dataTypes.contains("Boolean"));
        assertTrue(dataTypes.contains("String"));
        assertTrue(dataTypes.contains("Integer"));
        assertTrue(dataTypes.contains("Double"));
        assertTrue(dataTypes.contains("Float"));
        assertTrue(dataTypes.contains("java.util.List"));

    }
}
