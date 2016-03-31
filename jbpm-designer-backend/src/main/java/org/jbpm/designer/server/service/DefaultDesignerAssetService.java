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
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.*;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.di.DiagramElement;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.async.TimedTask;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshaller;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.jbpm.designer.model.*;
import org.jbpm.designer.model.Task;
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
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
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

    private Path createProcess( final Path context,
                               final String fileName,
                               final String processContent ) {

        final Path path = Paths.convert( Paths.convert( context ).resolve( fileName ) );
        String location = Paths.convert( path ).getParent().toString();
        String name = path.getFileName();

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder( name );
        builder.location( location ).content( processContent ).uniqueId( path.toURI() );
        Asset<String> processAsset = builder.getAsset();

        repository.createAsset( processAsset );
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
        return createProcess(context, fileName, processContent);
    }

    @Override
    public Path createProcess(Path context, String fileName, BusinessProcess businessProcess) {
        createProcess(businessProcess.getProcessName(), businessProcess.getProcessDocumentation());
        createProcessVariables(businessProcess.getVariables());
        int horizontalOffset = 100;
        FlowElement from = createStartEvent(horizontalOffset, businessProcess.getStartEvent());
        FlowElement to;
        for(Integer row : businessProcess.getTasks().keySet()) {
            if(businessProcess.getTasks().get(row).size() > 1) {
                horizontalOffset += 150;
                FlowElement fromGateway = createDivergingGateway(horizontalOffset);
                FlowElement toGateway = createConvergingGateway(horizontalOffset + 300);
                createEdge(from, fromGateway, null);
                int verticalRelativeOffset = 0;
                for(Task task : businessProcess.getTasks().get(row)) {
                    FlowElement middle = createTask(task, horizontalOffset + 150, verticalRelativeOffset);
                    if(task.getCondition() != null) {
                        createEdge(fromGateway, middle, task.getCondition().getConstraint());
                    } else {
                        createEdge(fromGateway, middle, null);
                    }
                    createEdge(middle, toGateway, null);
                    verticalRelativeOffset += 150;
                }
                from = toGateway;
                horizontalOffset = horizontalOffset + 300;
            } else {
                for (Task task : businessProcess.getTasks().get(row)) {
                    horizontalOffset += 150;
                    to = createTask(task, horizontalOffset, 0);
                    createEdge(from, to, null);
                    from = to;
                }
            }
        }
        to = createEndEvent(horizontalOffset + 150);
        createEdge(from, to, null);
        return createProcess(context, fileName, convertProcessToXml());
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

    private Process process = null;
    private BPMNDiagram diagram = null;
    private Definitions definitions = null;
    private JbpmProfileImpl profile = new JbpmProfileImpl();

    private String createProcess(String processName, String documentationText) {

        Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
        marshaller.setProfile(profile);

        String processId = UUID.randomUUID().toString();
        process = Bpmn2Factory.eINSTANCE.createProcess();
        process.setId(processId);
        process.setName(processName);
        Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
        documentation.setText(documentationText);
        process.getDocumentation().add(documentation);

        diagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
        BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
        plane.setBpmnElement(process);
        diagram.setPlane(plane);

        definitions = Bpmn2Factory.eINSTANCE.createDefinitions();
        definitions.getRootElements().add(process);
        definitions.getDiagrams().add(diagram);

        return processId;
    }

    private FlowElement createTask(Task task, int horizontalOffset, int verticalRelativeOffset) {
        if (process == null) {
            throw new RuntimeException("Create process at first");
        }

        String taskId = UUID.randomUUID().toString();
        org.eclipse.bpmn2.Task bpmnTask = null;
        if (Task.HUMAN_TYPE.compareTo(task.getTaskType()) == 0) {
            bpmnTask = Bpmn2Factory.eINSTANCE.createUserTask();
            bpmnTask.setId(taskId);
            bpmnTask.setName(task.getName());
        }

        if (Task.SERVICE_TYPE.compareTo(task.getTaskType()) == 0) {
            bpmnTask = Bpmn2Factory.eINSTANCE.createServiceTask();
            bpmnTask.setId(taskId);
            bpmnTask.setName(task.getName());
        }

        BPMNShape shape = getShape(100, 80, horizontalOffset, 100 + verticalRelativeOffset);
        shape.setBpmnElement(bpmnTask);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(bpmnTask);

        return bpmnTask;
    }

    private void createProcessVariables(List<Variable> variables) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        ItemDefinition stringDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        stringDefinition.setId(UUID.randomUUID().toString());
        stringDefinition.setStructureRef("String");

        ItemDefinition booleanDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        booleanDefinition.setId(UUID.randomUUID().toString());
        booleanDefinition.setStructureRef("Boolean");

        ItemDefinition integerDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        integerDefinition.setId(UUID.randomUUID().toString());
        integerDefinition.setStructureRef("Integer");

        for(Variable variable : variables) {
            Property property = Bpmn2Factory.eINSTANCE.createProperty();
            property.setId(variable.getName());
            property.setName(variable.getName());
            if(variable.getDataType().compareTo("string") == 0) {
                property.setItemSubjectRef(stringDefinition);
            }
            if(variable.getDataType().compareTo("boolean") == 0) {
                property.setItemSubjectRef(booleanDefinition);
            }
            if(variable.getDataType().compareTo("number") == 0) {
                property.setItemSubjectRef(integerDefinition);
            }
            process.getProperties().add(property);
        }
    }

    private FlowElement createDivergingGateway(int horizontalOffset) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();
        ExclusiveGateway gateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
        gateway.setGatewayDirection(GatewayDirection.DIVERGING);
        gateway.setId(id);

        BPMNShape shape = getShape(40, 40, horizontalOffset, 100);
        shape.setBpmnElement(gateway);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(gateway);
        return gateway;
    }

    private FlowElement createConvergingGateway(int horizontalOffset) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();
        ExclusiveGateway gateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
        gateway.setGatewayDirection(GatewayDirection.CONVERGING);
        gateway.setId(id);

        BPMNShape shape = getShape(40, 40, horizontalOffset, 100);
        shape.setBpmnElement(gateway);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(gateway);
        return gateway;
    }

    private FlowElement createEdge(FlowElement from, FlowElement to, Constraint constraint) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        BPMNShape fromShape = getShapeByElement(from);
        BPMNShape toShape = getShapeByElement(to);

        String id = UUID.randomUUID().toString();
        SequenceFlow flow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
        flow.setSourceRef((FlowNode) from);
        flow.setTargetRef((FlowNode) to);
        flow.setId(id);

        if(constraint != null) {
            FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            expression.setId(UUID.randomUUID().toString());
            String expressionBody = "return KieFunctions.equalsTo(";
            expressionBody += constraint.getVariable().getName();
            expressionBody += ",\"";
            expressionBody += constraint.getConstraintValue();
            expressionBody += "\");";
            expression.setBody(expressionBody);
            flow.setConditionExpression(expression);
        }

        BPMNEdge edge = BpmnDiFactory.eINSTANCE.createBPMNEdge();
        edge.setSourceElement(fromShape);
        edge.setTargetElement(toShape);
        edge.setBpmnElement(flow);

        diagram.getPlane().getPlaneElement().add(edge);
        process.getFlowElements().add(flow);
        return flow;
    }

    private BPMNShape getShapeByElement(FlowElement element) {
        for(DiagramElement shape : diagram.getPlane().getPlaneElement()) {

            if((shape instanceof BPMNShape) && ((BPMNShape)shape).getBpmnElement() == element) {
                return (BPMNShape) shape;
            }
        }

        return null;
    }

    private FlowElement createStartEvent(int horizontalOffset, StandardEvent startEvent) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();

        StartEvent start = Bpmn2Factory.eINSTANCE.createStartEvent();
        start.setId(id);
        if(startEvent instanceof SignalEvent) {
            Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
            signal.setName(((SignalEvent)startEvent).getSignalName());
            signal.setId(UUID.randomUUID().toString());
            definitions.getRootElements().add(signal);
            SignalEventDefinition signalED = Bpmn2Factory.eINSTANCE.createSignalEventDefinition();
            signalED.setSignalRef(signal.getId());
            signalED.setId(UUID.randomUUID().toString());
            start.getEventDefinitions().add(signalED);
        }
        if(startEvent instanceof TimerEvent) {
            TimerEventDefinition timer = Bpmn2Factory.eINSTANCE.createTimerEventDefinition();
            String type = ((TimerEvent)startEvent).getTimerType();
            FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            expression.setBody(((TimerEvent)startEvent).getTimerExpression());
            expression.setId(UUID.randomUUID().toString());
            if(TimerEvent.DURATION.compareTo(type) == 0) {
                timer.setTimeDuration(expression);
            }
            if(TimerEvent.CYCLE.compareTo(type) == 0) {
                timer.setTimeCycle(expression);
            }
            if(TimerEvent.DATE.compareTo(type) == 0) {
                timer.setTimeDate(expression);
            }
            timer.setId(UUID.randomUUID().toString());
            start.getEventDefinitions().add(timer);
        }

        BPMNShape shape = getShape(28, 28, horizontalOffset, 100);
        shape.setBpmnElement(start);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(start);
        return start;
    }

    private FlowElement createEndEvent(int horizontalOffset) {
        if(process == null) {
            throw new RuntimeException("Create process at first");
        }

        String id = UUID.randomUUID().toString();
        EndEvent end = Bpmn2Factory.eINSTANCE.createEndEvent();
        end.setId(id);

        BPMNShape shape = getShape(28, 28, horizontalOffset, 100);
        shape.setBpmnElement(end);

        diagram.getPlane().getPlaneElement().add(shape);
        process.getFlowElements().add(end);
        return end;
    }

    private String convertProcessToXml() {
        if(process == null ) {
            throw new RuntimeException("Create process at first");
        }
        Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
        marshaller.setProfile(profile);

        try {
            return profile.createMarshaller().parseModel(marshaller.marshall(definitions, ""), "");
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert your model to xml", e);
        }
    }

    private BPMNShape getShape(int width, int height, int x, int y) {
        BPMNShape shape = BpmnDiFactory.eINSTANCE.createBPMNShape();
        Bounds bounds = DcFactory.eINSTANCE.createBounds();
        bounds.setWidth(width);
        bounds.setHeight(height);
        bounds.setX(x);
        bounds.setY(y);

        shape.setBounds(bounds);
        return shape;
    }
}
