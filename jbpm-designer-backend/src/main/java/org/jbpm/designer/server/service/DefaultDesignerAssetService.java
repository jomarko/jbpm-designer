/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.server.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.operation.Swagger;
import org.jbpm.designer.model.operation.SwaggerDefinition;
import org.jbpm.designer.model.operation.SwaggerParameter;
import org.jbpm.designer.model.operation.SwaggerProperty;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.service.BPMN2DataServices;
import org.jbpm.designer.service.DesignerAssetService;
import org.jbpm.designer.service.DesignerContent;
import org.jbpm.designer.util.Utils;
import org.json.JSONArray;
import org.kie.workbench.common.services.backend.service.KieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class DefaultDesignerAssetService
        extends KieService<DesignerContent>
        implements DesignerAssetService {

    private static Logger logger = LoggerFactory.getLogger(DefaultDesignerAssetService.class);

    @Inject
    private Repository repository;
    
    @Inject
    private Instance<BPMN2DataServices> bpmn2DataServices;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryDescriptor descriptor;

    @Inject
    protected WizardModelToXmlConverter wizardModelToXmlConverter;
   
    // socket buffer size in bytes: can be tuned for performance
    private final static int socketBufferSize = 8 * 1024;

    private static final Logger _logger =
            LoggerFactory.getLogger(DefaultDesignerAssetService.class);

    public static final String PROCESS_STUB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
    "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:drools=\"http://www.jboss.org/drools\" \n" +
        "id=\"Definition\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" expressionLanguage=\"http://www.mvel.org/2.0\" targetNamespace=\"http://www.omg.org/bpmn20\" typeLanguage=\"http://www.java.com/javaTypes\"> \n" +
    "   <bpmn2:process id=\"${processid}\" drools:packageName=\"org.jbpm\" drools:version=\"1.0\" name=\"\" isExecutable=\"true\"> \n" +
    "      <bpmn2:startEvent id=\"processStartEvent\" drools:bgcolor=\"#9acd32\" drools:selectable=\"true\" name=\"\"/> \n" +
    "   </bpmn2:process> \n" +
    "   <bpmndi:BPMNDiagram> \n" +
    "      <bpmndi:BPMNPlane bpmnElement=\"${processid}\"> \n" +
    "         <bpmndi:BPMNShape bpmnElement=\"processStartEvent\"> \n" +
    "            <dc:Bounds height=\"30.0\" width=\"30.0\" x=\"120.0\" y=\"165.0\"/> \n" +
    "         </bpmndi:BPMNShape> \n" +
    "      </bpmndi:BPMNPlane> \n" +
    "   </bpmndi:BPMNDiagram> \n" +
    "</bpmn2:definitions>";

    @Override
    public DesignerContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    public Map<String, String> getEditorParameters( final Path path,
                                  final String editorID,
                                  String hostInfo,
                                  PlaceRequest place ) {
        List<String> activeNodesList = new ArrayList<String>();
        String activeNodesParam = place.getParameter( "activeNodes", null );

        boolean readOnly = place.getParameter( "readOnly", null ) != null;

        if(!readOnly) {
            try {
                ioService.getFileSystem(URI.create(path.toURI()));
            } catch(Exception e) {
                logger.error("Unable to create file system: " + e.getMessage());
                throw new FileSystemNotFoundException(e.getMessage());
            }
        }


        String processId = place.getParameter( "processId", "" );
        String deploymentId = place.getParameter( "deploymentId", "" );
        String encodedProcessSource = "";
        try {
            encodedProcessSource = bpmn2DataServices.iterator().next().getProcessSources(deploymentId,  processId );
        } catch(Exception e) {
            encodedProcessSource = place.getParameter( "encodedProcessSource", "" );
        }
        
        if ( activeNodesParam != null ) {
            activeNodesList = Arrays.asList( activeNodesParam.split( "," ) );
        }

        List<String> completedNodesList = new ArrayList<String>();
        String completedNodesParam = place.getParameter( "completedNodes", null );

        if ( completedNodesParam != null ) {
            completedNodesList = Arrays.asList( completedNodesParam.split( "," ) );
        }

        JSONArray activeNodesArray = new JSONArray( activeNodesList );
//        String encodedActiveNodesParam;
//        try {
//            encodedActiveNodesParam = Base64.encodeBase64URLSafeString( activeNodesArray.toString().getBytes( "UTF-8" ) );
//        } catch ( UnsupportedEncodingException e ) {
//            encodedActiveNodesParam = "";
//        }

        JSONArray completedNodesArray = new JSONArray( completedNodesList );
//        String encodedCompletedNodesParam;
//        try {
//            encodedCompletedNodesParam = Base64.encodeBase64URLSafeString( completedNodesArray.toString().getBytes( "UTF-8" ) );
//        } catch ( UnsupportedEncodingException e ) {
//            encodedCompletedNodesParam = "";
//        }

        Map<String, String> editorParamsMap = new HashMap<String, String>();
        editorParamsMap.put("hostinfo", hostInfo);
        try {
            editorParamsMap.put("uuid", Base64.encodeBase64URLSafeString(UriUtils.decode(path.toURI()).getBytes("UTF-8")));
        } catch(UnsupportedEncodingException e) {

        }
        editorParamsMap.put("profile", "jbpm");
        editorParamsMap.put("pp", "");
        editorParamsMap.put("editorid", editorID);
        editorParamsMap.put("readonly", String.valueOf(readOnly));
        editorParamsMap.put("activenodes", activeNodesArray.toString());
        editorParamsMap.put("completednodes", completedNodesArray.toString());
        editorParamsMap.put("processsource", encodedProcessSource);

        //Signal opening to interested parties if we are not in readonly mode
        if(!readOnly) {
            resourceOpenedEvent.fire(new ResourceOpenedEvent( path, sessionInfo ));
        }

        return editorParamsMap;
    }

    @Override
    public String getEditorID() {
        return UUID.randomUUID().toString().replaceAll( "-", "" );
    }

    private Path createAsset( final Path context,
                               final String fileName,
                               final String assetContent ) {

        final Path path = Paths.convert( Paths.convert( context ).resolve( fileName ) );
        String location = Paths.convert( path ).getParent().toString();
        String name = path.getFileName();

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder( name );
        builder.location( location ).content( assetContent ).uniqueId( path.toURI() );
        Asset<String> asset = builder.getAsset();

        repository.createAsset( asset );
        return path;
    }

    @Override
    public Path createProcess( final Path context,
                               final String fileName ) {

        final Path path = Paths.convert( Paths.convert( context ).resolve( fileName ) );
        String location = Paths.convert( path ).getParent().toString();
        String name = path.getFileName();
        String processId = buildProcessId( location, name );
        String processContent = PROCESS_STUB.replaceAll( "\\$\\{processid\\}", processId.replaceAll("\\s", "") );
        return createAsset(context, fileName, processContent);
    }

    @Override
    public Path createProcess(Path context, String fileName, BusinessProcess businessProcess) {
        String processContent = wizardModelToXmlConverter.convertProcessToXml(businessProcess);
        String processId = wizardModelToXmlConverter.getProcessId();

        String formName = processId + "-taskform.form";
        String formContent= createFormContent(businessProcess, formName);
        createAsset(context, formName, formContent);
        return createAsset(context, fileName, processContent);
    }

    private String createFormContent(BusinessProcess process, String formName) {
        String formContent = "<form id=\""+ new Random(formName.hashCode()).nextLong() +"\">\n" +
                "<property name=\"name\" value=\"" + formName +"\"/>\n" +
                "<property name=\"displayMode\" value=\"default\"/>\n" +
                "<property name=\"status\" value=\"0\"/>\n" +
                "${fields}\n" +
                "${dataHolders}\n" +
                "</form>";

        formContent = formContent.replaceAll( "\\$\\{fields\\}", constructFields(process.getVariables(), process.getDefinitions()));
        formContent = formContent.replaceAll( "\\$\\{dataHolders\\}", constructDataHolders(process.getVariables()));

        return formContent;
    }

    private String constructFields(List<Variable> variables, Map<String, SwaggerDefinition> definitions) {
        String fields = "";
        int count = 0;
        for(Variable variable : variables) {
            if(isBasicDataType(variable)) {
                fields += "<field position=\"" + count + "\" name=\"" + variable.getName() + "\" type=\""+getFieldType(variable.getDataType())+"\" id=\"" + new Random(variable.getName().hashCode()).nextLong() + "\">\n" +
                        "<property name=\"fieldRequired\" value=\"true\"/>\n" +
                        "<property name=\"label\" value=\"quot;enquot;,quot;" +variable.getName()+ "quot;\"/>\n" +
                        "<property name=\"readonly\" value=\"false\"/>\n" +
                        "<property name=\"outputBinding\" value=\"" + variable.getName() + "\"/>\n" +
                        "<property name=\"fieldClass\" value=\"java.lang." + variable.getDataType() + "\"/>\n" +
                        "</field>";
            } else {
                String[] dataTypeParts = variable.getDataType().split("\\.");
                if(dataTypeParts.length > 0 && definitions.keySet().contains(dataTypeParts[dataTypeParts.length - 1])) {
                    SwaggerDefinition definition = definitions.get(dataTypeParts[dataTypeParts.length - 1]);
                    if(definition != null) {
                        for (Map.Entry<String, SwaggerProperty> property : definition.getProperties().entrySet()) {
                            fields += "<field position=\"" + count + "\" name=\"" + variable.getName() + "_" + property.getKey() + "\" type=\""+getFieldType(property.getValue().getType()) + "\" id=\"" + new Random(variable.getName().hashCode()).nextLong() + "\">\n" +
                                    "<property name=\"fieldRequired\" value=\"" + (definition.getRequired().contains(property.getKey()) ? "true" : "false") + "\"/>\n" +
                                    "<property name=\"label\" value=\"quot;enquot;,quot;" + variable.getName() + "/" + property.getKey() +"quot;\"/>\n" +
                                    "<property name=\"readonly\" value=\"false\"/>\n" +
                                    "<property name=\"outputBinding\" value=\"" + variable.getName() + "/" + property.getKey() + "\"/>\n" +
                                    "<property name=\"fieldClass\" value=\"java.lang." + makeUpperCase(property.getValue().getType()) + "\"/>\n" +
                                    "</field>";
                            count = count + 1;
                        }
                    }
                }
            }

            count = count + 1;
        }
        return fields;
    }

    private String constructDataHolders(List<Variable> variables) {
        String dataHolders = "";
        for(Variable variable : variables) {
            if(isBasicDataType(variable)) {
                dataHolders += "<dataHolder inputId=\"\" name=\"" + new Random(variable.getName().hashCode()).nextLong() + "\" type=\"basicType\" outId=\"" + variable.getName() + "\" value=\"java.lang." + variable.getDataType() + "\" id=\"" + variable.getName() + "\"/>\n";
            } else {
                dataHolders += "<dataHolder inputId=\"\" name=\"" + new Random(variable.getName().hashCode()).nextLong() + "\" type=\"dataModelerEntry\" outId=\"" + variable.getName() + "\" value=\"" + variable.getDataType() + "\" id=\"" + variable.getName() + "\"/>\n";
            }

        }
        return dataHolders;
    }

    private String getFieldType(String dataType) {
        if(dataType.compareToIgnoreCase("boolean") == 0) {
            return  "CheckBox";
        }
        if(dataType.compareToIgnoreCase("float") == 0) {
            return  "InputTextFloat";
        }
        if(dataType.compareToIgnoreCase("double") == 0) {
            return  "InputTextDouble";
        }
        if(dataType.compareToIgnoreCase("integer") == 0) {
            return  "InputTextBigInteger";
        }
        return "InputText";
    }

    private String makeUpperCase(String type) {
        if(type.length() < 2) {
            return type.toUpperCase();
        }
        return type.substring(0,1).toUpperCase() + type.substring(1, type.length());
    }

    private boolean isBasicDataType(Variable variable) {
        String dataType = variable.getDataType();
        if(dataType.compareTo("Object") == 0 ||
                dataType.compareTo("Boolean") == 0 ||
                dataType.compareTo("Integer") == 0 ||
                dataType.compareTo("Float") == 0 ||
                dataType.compareTo("Double") == 0 ||
                dataType.compareTo("String") == 0 ) {
            return true;
        }
        return false;
    }

    private String getEditorResponse(String urlpath,
                                     String encProcessSrc ) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // convert string to url in order to get host and port
        URL url;
        try {
            url = new URL(urlpath);
        } catch( MalformedURLException murle ) {
            logger.error( "Incorrect URL: " + murle.getMessage(), murle );
            return null;
        }

        // configure socket to ignore local addresses (this constructur instead of full constructor)
        Socket socket;
        try {
            socket = new Socket(url.getHost(), url.getPort());
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(socketBufferSize);
        conn.bind(socket);
        } catch( Exception  e ) {
            e.printStackTrace();
        }

        // TODO: tiho, if it's possible to do preemptive basic authentication here (which it is?, I think?), please let me know.
        // Then you can do everything in one request, which will improve performance.. :) -- mriet

        // setup form authentication
        List<NameValuePair> formParams = new ArrayList<NameValuePair>(2);
        formParams.add(new BasicNameValuePair("j_username", "admin"));
        formParams.add(new BasicNameValuePair("j_password", "admin"));
        UrlEncodedFormEntity formEntity;
        try {
            formEntity = new UrlEncodedFormEntity(formParams);
        } catch( UnsupportedEncodingException uee ) {
            logger.error("Could not encode authentication parameters into request body", uee);
            return null;
        }

        // do form authentication
        HttpPost authMethod = new HttpPost(urlpath);
        authMethod.setEntity(formEntity);
        try {
            httpClient.execute(authMethod);
        } catch (IOException ioe) {
            logger.error("Could not initialize form-based authentication", ioe);
            return null;
        } finally {
            authMethod.releaseConnection();
        }

        // create post method and add query parameter
        HttpPost theMethod = new HttpPost( urlpath );
        BasicHttpParams params = new BasicHttpParams();
        params.setParameter( "processsource", encProcessSrc );
        theMethod.setParams(params);

        // execute post method and return response content
        try {
            // post
            CloseableHttpResponse response = httpClient.execute( theMethod );

            // extract content
            HttpEntity respEntity = response.getEntity();
            String responseBody = null;
            if( respEntity != null ) {
                responseBody = EntityUtils.toString(respEntity);
            }
            return responseBody;
        } catch ( Exception e ) {
            logger.error("Could not do POST method and retrieve content: " + e.getMessage(), e);
            return null;
        } finally {
            theMethod.releaseConnection();
        }
    }

    private String buildProcessId( String location,
                                   String name ) {
        if ( location.startsWith( "/" ) ) {
            location = location.replaceFirst( "/", "" );
        }
        location = location.replaceAll( "/", "." );
            
        if(location.length() > 0) {
       String[] locationParts = location.split("\\.");
            location = locationParts[0];
        }

        name = name.substring( 0, name.lastIndexOf( "." ) );
        name = Utils.toBPMNIdentifier(name);
        return location + "." + name;
    }

    @Override
    public void updateMetadata( final Path resource, final Metadata metadata ) {
        ioService.setAttributes( Paths.convert( resource ),
                metadataService.setUpAttributes( resource, metadata ) );
    }

    @Override
    protected DesignerContent constructContent(Path path, Overview overview) {
        return new DesignerContent(overview);
    }
}
