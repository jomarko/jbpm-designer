package org.jbpm.designer.client.wizard.pages.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.DesignerWidgetPresenter;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

@Dependent
public class ProcessPreviewPage implements WizardPage {

    private PlaceRequest place;

    @Inject
    DesignerWidgetPresenter designerWidget;

    @Inject
    private Caller<DesignerAssetService> assetService;

    @Inject
    private DesignerEditorParametersPublisher designerEditorParametersPublisher;

    @Override
    public String getTitle() {
        return "Preview";
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        callback.callback(true);
    }

    @Override
    public void initialise() {
        if ( place instanceof PathPlaceRequest) {
            assetService.call( new RemoteCallback<String>() {
                @Override
                public void callback( final String editorID ) {
                    String url = GWT.getHostPageBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" );
                    assetService.call( new RemoteCallback<Map<String, String>>() {
                        @Override
                        public void callback( Map<String, String> editorParameters ) {
                            if ( editorParameters != null ) {
                                editorParameters.put("readonly", "true");
                                designerEditorParametersPublisher.publish(editorParameters);
                                designerWidget.setup( editorID, editorParameters );
                            }
                        }

                    } ).getEditorParameters( ( (PathPlaceRequest) place ).getPath(), editorID, url, place );
                }
            } ).getEditorID();
        }
    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return designerWidget.getView().asWidget();
    }

    public void setPlace(PlaceRequest place) {
        this.place = place;
    }
}
