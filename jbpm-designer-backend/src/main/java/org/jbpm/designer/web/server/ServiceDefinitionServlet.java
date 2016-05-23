package org.jbpm.designer.web.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;
import org.jbpm.designer.service.SwaggerService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServiceDefinitionServlet extends HttpServlet {

    @Inject
    SwaggerService swaggerService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream fileContent = null;
        try {
            response.setContentType( "text/html" );
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            StringBuilder content= new StringBuilder(8192);;
            String fileName = "";
            String packageName = "";
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    fileName = item.getName();
                    fileContent = item.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(fileContent, StandardCharsets.UTF_8));
                    String part = null;
                    while ((part = r.readLine()) != null) {
                        content.append(part);
                    }
                } else if(item.getFieldName().compareTo("packageName") == 0) {
                    packageName = item.getString();
                }
            }

            ServiceUploadResultEntry resultEntry = swaggerService.createSwagger(packageName, fileName, content.toString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
            response.getWriter().write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultEntry));
        } catch (FileUploadException e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() +"\"}");
        } finally {
            if(fileContent != null) {
                fileContent.close();
            }
        }
    }
}
