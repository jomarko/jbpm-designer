package org.jbpm.designer.client.wizard.util;


import org.jbpm.designer.model.operation.SwaggerSchema;

public class CompareUtils {
    public static boolean areSchemeAndDataTypeSame(SwaggerSchema schema, String dataType) {
        if(schema  != null && schema.get$ref() != null && dataType != null) {
            String[] refParts = schema.get$ref().split("/");
            String[] dataTypesParts = dataType.split("\\.");
            if( refParts.length > 0 && dataTypesParts.length > 0 &&
                    refParts[refParts.length - 1].compareTo(dataTypesParts[dataTypesParts.length - 1]) == 0) {
                return true;
            }
        }

        return false;
    }
}
