package org.jbpm.designer.server.service;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.model.operation.Swagger;
import org.jbpm.designer.service.SwaggerService;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@Service
@ApplicationScoped
public class DefaultSwaggerService implements SwaggerService {
    @Override
    public Swagger getSwagger() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Swagger swagger = mapper.readValue(mockJson, Swagger.class);
        swagger.setUrlBase("http://private-e985ca-diplomathesis.apiary-mock.com/api");
        return swagger;
    }

    private String mockJson = "\n" +
            "\n" +
            "{\n" +
            "  \"swagger\" : \"2.0\",\n" +
            "  \"info\" : {\n" +
            "    \"description\" : \"A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification\",\n" +
            "    \"version\" : \"1.0.0\",\n" +
            "    \"title\" : \"diploma-thesis\",\n" +
            "    \"termsOfService\" : \"http://swagger.io/terms/\",\n" +
            "    \"contact\" : {\n" +
            "      \"name\" : \"Swagger API Team\",\n" +
            "      \"url\" : \"http://madskristensen.net\",\n" +
            "      \"email\" : \"foo@example.com\"\n" +
            "    },\n" +
            "    \"license\" : {\n" +
            "      \"name\" : \"MIT\",\n" +
            "      \"url\" : \"http://github.com/gruntjs/grunt/blob/master/LICENSE-MIT\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"host\" : \"petstore.swagger.io\",\n" +
            "  \"basePath\" : \"/api\",\n" +
            "  \"schemes\" : [ \"http\" ],\n" +
            "  \"consumes\" : [ \"application/json\" ],\n" +
            "  \"produces\" : [ \"application/json\" ],\n" +
            "  \"paths\" : {\n" +
            "    \"/pets\" : {\n" +
            "      \"get\" : {\n" +
            "        \"description\" : \"Returns all pets from the system that the user has access to\\nNam sed condimentum est. Maecenas tempor sagittis sapien, nec rhoncus sem sagittis sit amet. Aenean at gravida augue, ac iaculis sem. Curabitur odio lorem, ornare eget elementum nec, cursus id lectus. Duis mi turpis, pulvinar ac eros ac, tincidunt varius justo. In hac habitasse platea dictumst. Integer at adipiscing ante, a sagittis ligula. Aenean pharetra tempor ante molestie imperdiet. Vivamus id aliquam diam. Cras quis velit non tortor eleifend sagittis. Praesent at enim pharetra urna volutpat venenatis eget eget mauris. In eleifend fermentum facilisis. Praesent enim enim, gravida ac sodales sed, placerat id erat. Suspendisse lacus dolor, consectetur non augue vel, vehicula interdum libero. Morbi euismod sagittis libero sed lacinia.\\nSed tempus felis lobortis leo pulvinar rutrum. Nam mattis velit nisl, eu condimentum ligula luctus nec. Phasellus semper velit eget aliquet faucibus. In a mattis elit. Phasellus vel urna viverra, condimentum lorem id, rhoncus nibh. Ut pellentesque posuere elementum. Sed a varius odio. Morbi rhoncus ligula libero, vel eleifend nunc tristique vitae. Fusce et sem dui. Aenean nec scelerisque tortor. Fusce malesuada accumsan magna vel tempus. Quisque mollis felis eu dolor tristique, sit amet auctor felis gravida. Sed libero lorem, molestie sed nisl in, accumsan tempor nisi. Fusce sollicitudin massa ut lacinia mattis. Sed vel eleifend lorem. Pellentesque vitae felis pretium, pulvinar elit eu, euismod sapien.\\n\",\n" +
            "        \"operationId\" : \"findPets\",\n" +
            "        \"parameters\" : [ {\n" +
            "          \"name\" : \"tags\",\n" +
            "          \"in\" : \"query\",\n" +
            "          \"description\" : \"tags to filter by\",\n" +
            "          \"required\" : false,\n" +
            "          \"type\" : \"array\",\n" +
            "          \"items\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"collectionFormat\" : \"csv\"\n" +
            "        }, {\n" +
            "          \"name\" : \"limit\",\n" +
            "          \"in\" : \"query\",\n" +
            "          \"description\" : \"maximum number of results to return\",\n" +
            "          \"required\" : false,\n" +
            "          \"type\" : \"integer\",\n" +
            "          \"format\" : \"int32\"\n" +
            "        } ],\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"pet response\",\n" +
            "            \"schema\" : {\n" +
            "              \"type\" : \"array\",\n" +
            "              \"items\" : {\n" +
            "                \"$ref\" : \"#/definitions/Pet\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"default\" : {\n" +
            "            \"description\" : \"unexpected error\",\n" +
            "            \"schema\" : {\n" +
            "              \"$ref\" : \"#/definitions/Error\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"post\" : {\n" +
            "        \"description\" : \"Creates a new pet in the store.  Duplicates are allowed\",\n" +
            "        \"operationId\" : \"addPet\",\n" +
            "        \"parameters\" : [ {\n" +
            "          \"in\" : \"body\",\n" +
            "          \"name\" : \"pet\",\n" +
            "          \"description\" : \"Pet to add to the store\",\n" +
            "          \"required\" : true,\n" +
            "          \"schema\" : {\n" +
            "            \"$ref\" : \"#/definitions/NewPet\"\n" +
            "          }\n" +
            "        } ],\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"pet response\",\n" +
            "            \"schema\" : {\n" +
            "              \"$ref\" : \"#/definitions/Pet\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"default\" : {\n" +
            "            \"description\" : \"unexpected error\",\n" +
            "            \"schema\" : {\n" +
            "              \"$ref\" : \"#/definitions/Error\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"/pets/{id}\" : {\n" +
            "      \"get\" : {\n" +
            "        \"description\" : \"Returns a user based on a single ID, if the user does not have access to the pet\",\n" +
            "        \"operationId\" : \"find pet by id\",\n" +
            "        \"parameters\" : [ {\n" +
            "          \"name\" : \"id\",\n" +
            "          \"in\" : \"path\",\n" +
            "          \"description\" : \"ID of pet to fetch\",\n" +
            "          \"required\" : true,\n" +
            "          \"type\" : \"integer\",\n" +
            "          \"format\" : \"int64\"\n" +
            "        } ],\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"pet response\",\n" +
            "            \"schema\" : {\n" +
            "              \"$ref\" : \"#/definitions/Pet\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"default\" : {\n" +
            "            \"description\" : \"unexpected error\",\n" +
            "            \"schema\" : {\n" +
            "              \"$ref\" : \"#/definitions/Error\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"delete\" : {\n" +
            "        \"description\" : \"deletes a single pet based on the ID supplied\",\n" +
            "        \"operationId\" : \"deletePet\",\n" +
            "        \"parameters\" : [ {\n" +
            "          \"name\" : \"id\",\n" +
            "          \"in\" : \"path\",\n" +
            "          \"description\" : \"ID of pet to delete\",\n" +
            "          \"required\" : true,\n" +
            "          \"type\" : \"integer\",\n" +
            "          \"format\" : \"int64\"\n" +
            "        } ],\n" +
            "        \"responses\" : {\n" +
            "          \"204\" : {\n" +
            "            \"description\" : \"pet deleted\"\n" +
            "          },\n" +
            "          \"default\" : {\n" +
            "            \"description\" : \"unexpected error\",\n" +
            "            \"schema\" : {\n" +
            "              \"$ref\" : \"#/definitions/Error\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"definitions\" : {\n" +
            "    \"Pet\" : {\n" +
            "      \"allOf\" : [ {\n" +
            "        \"$ref\" : \"#/definitions/NewPet\"\n" +
            "      }, {\n" +
            "        \"required\" : [ \"id\" ],\n" +
            "        \"properties\" : {\n" +
            "          \"id\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          }\n" +
            "        }\n" +
            "      } ]\n" +
            "    },\n" +
            "    \"NewPet\" : {\n" +
            "      \"required\" : [ \"name\" ],\n" +
            "      \"properties\" : {\n" +
            "        \"name\" : {\n" +
            "          \"type\" : \"string\"\n" +
            "        },\n" +
            "        \"tag\" : {\n" +
            "          \"type\" : \"string\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"Error\" : {\n" +
            "      \"required\" : [ \"code\", \"message\" ],\n" +
            "      \"properties\" : {\n" +
            "        \"code\" : {\n" +
            "          \"type\" : \"integer\",\n" +
            "          \"format\" : \"int32\"\n" +
            "        },\n" +
            "        \"message\" : {\n" +
            "          \"type\" : \"string\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n";
}
