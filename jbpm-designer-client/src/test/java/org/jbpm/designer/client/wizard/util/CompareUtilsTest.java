package org.jbpm.designer.client.wizard.util;

import org.jbpm.designer.model.operation.SwaggerSchema;
import static org.junit.Assert.*;
import org.junit.Test;

public class CompareUtilsTest {
    @Test
    public void testAreSameNull() throws Exception {
        SwaggerSchema schema = null;
        String dataType = null;
        assertFalse(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

    @Test
    public void testAreSameEmpty() throws Exception {
        SwaggerSchema schema = new SwaggerSchema();
        schema.set$ref("   ");
        String dataType = "  ";
        assertFalse(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

    @Test
    public void testAreSameDifferent() throws Exception {
        SwaggerSchema schema = new SwaggerSchema();
        schema.set$ref("#def/Pet");
        String dataType = "org.NewPet";
        assertFalse(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

    @Test
    public void testAreSame() throws Exception {
        SwaggerSchema schema = new SwaggerSchema();
        schema.set$ref("#def/Pet");
        String dataType = "org.Pet";
        assertTrue(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

    @Test
    public void testAreSameNoDelimiters() throws Exception {
        SwaggerSchema schema = new SwaggerSchema();
        schema.set$ref("Pet");
        String dataType = "Pet";
        assertTrue(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

    @Test
    public void testAreSameNoSchemaDelimiters() throws Exception {
        SwaggerSchema schema = new SwaggerSchema();
        schema.set$ref("Pet");
        String dataType = "org.Pet";
        assertTrue(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

    @Test
    public void testAreSameNoPackageDelimiters() throws Exception {
        SwaggerSchema schema = new SwaggerSchema();
        schema.set$ref("#def/Pet");
        String dataType = "Pet";
        assertTrue(CompareUtils.areSchemeAndDataTypeSame(schema, dataType));
    }

}
