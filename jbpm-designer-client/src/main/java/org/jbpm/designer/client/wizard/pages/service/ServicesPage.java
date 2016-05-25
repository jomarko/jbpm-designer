package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Form;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;
import org.jbpm.designer.model.operation.Swagger;
import org.jbpm.designer.model.operation.SwaggerDefinition;
import org.jbpm.designer.service.SwaggerService;
import org.kie.workbench.common.screens.search.model.SearchPageRow;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;
import org.kie.workbench.common.screens.search.service.SearchService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.paging.PageResponse;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
public class ServicesPage implements WizardPage, ServicesPageView.Presenter{

    @Inject
    ServicesPageView view;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.services();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        callback.callback(true);
    }

    @Inject
    Caller<SwaggerService> swaggerDefinitionService;

    @Inject
    Caller<SearchService> searchService;

    private List<Swagger> swaggers;

    @Override
    public void initialise() {
        view.init(this);
        view.clearUploadResults();
        swaggers = new ArrayList<Swagger>();
        findExistingSwaggers(0);
    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void handleSubmit(FormPanel.SubmitEvent submitEvent) {
        String fileName = view.getFileName();
        if ( fileName == null || "".equals( fileName ) ) {
            view.showSelectFileUploadWarning();
            submitEvent.cancel();
        } else if(fileName.contains(" ")) {
            view.showWhiteSpaceDisallowedWarning();
            submitEvent.cancel();
        } else if ( !( fileName.endsWith(".swagger") ) ) {
            view.showUnsupportedFileTypeWarning();
            submitEvent.cancel();
        } else {
            view.showUploadingBusy();
        }
    }

    @Override
    public void handleSubmitComplete(final Form.SubmitCompleteEvent submitCompleteEvent) {
        view.hideUploadingBusy();
        JSONValue result = JSONParser.parseStrict(submitCompleteEvent.getResults());
        ServiceUploadResultEntry entry = new ServiceUploadResultEntry();
        if(result.isObject() != null) {
            JSONObject objectResult = result.isObject();
            if(objectResult.get("status").toString().contains("ok")) {
                entry.setFileName(objectResult.get("fileName").toString());
                if(objectResult.containsKey("apiName")) {
                    entry.setApiName(objectResult.get("apiName").toString());
                }
                if(objectResult.containsKey("version")) {
                    entry.setVersion(objectResult.get("version").toString());
                }
                view.showUploadingResult(entry);
                swaggers.clear();
                view.clearUploadResults();
                findExistingSwaggers(0);
            } else {
                if(objectResult.containsKey("message")) {
                    view.showErrorReadingPath(objectResult.get("message").toString());
                } else {
                    view.showErrorReadingPath("Submit of swagger file was not successful");
                }
            }
        } else {
            view.showErrorReadingPath("Submit of swagger file was not successful");
        }
    }

    public List<Swagger> getSwaggers() {
        return swaggers;
    }

    public Map<String, SwaggerDefinition> getDefinitions() {
        Map<String, SwaggerDefinition> definitions = new HashMap<String, SwaggerDefinition>();
        if(swaggers != null) {
            for(Swagger swagger : swaggers) {
                definitions.putAll(swagger.getDefinitions());
            }
        }
        return definitions;
    }

    private void findExistingSwaggers(final int fromPage) {
        searchService.call(new RemoteCallback<PageResponse<SearchPageRow>>() {
            @Override
            public void callback(PageResponse<SearchPageRow> searchPageRowPageResponse) {
                for(SearchPageRow row : searchPageRowPageResponse.getPageRowList()) {
                    loadSwaggerFromPath(row.getPath());
                }
                if(searchPageRowPageResponse.getPageRowList().size() == 100) {
                    findExistingSwaggers(fromPage+1);
                }
            }
        }, new DefaultErrorCallback()).fullTextSearch(new SearchTermPageRequest("*.swagger", fromPage, 100));
    }

    private void loadSwaggerFromPath(final Path path) {
        try {
            swaggerDefinitionService.call(new RemoteCallback<Swagger>() {
                @Override
                public void callback(Swagger swagger) {
                    if(swagger != null) {
                        swaggers.add(swagger);
                        ServiceUploadResultEntry entry = new ServiceUploadResultEntry();
                        entry.setFileName(path.getFileName());
                        if(swagger.getInfo() != null) {
                            entry.setApiName(swagger.getInfo().getTitle());
                            entry.setVersion(swagger.getInfo().getVersion());
                        }
                        view.showUploadingResult(entry);
                    }
                }
            }, new DefaultErrorCallback()).getSwagger(path);
        } catch (IOException e) {
            view.showErrorReadingPath("Error : " + e.getMessage() + " during reading: " + path.getFileName());
        }
    }
}
