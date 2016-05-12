package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.gwt.FormPanel;
import org.gwtbootstrap3.client.ui.html.Text;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class ServicesPageViewImpl extends Composite implements ServicesPageView {

    interface ServicesPageViewImplBinder
            extends
            UiBinder<Widget, ServicesPageViewImpl> {
    }

    private static ServicesPageViewImplBinder uiBinder = GWT.create(ServicesPageViewImplBinder.class);

    public ServicesPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Inject
    ProjectContext projectContext;

    @Inject
    Event<NotificationEvent> notification;

    @UiField
    Form definitionUploadForm;

    @UiField
    FieldSet fileFieldSet;

    @UiField
    VerticalPanel results;

    private FileUpload uploader;

    private Presenter presenter;

    @PostConstruct
    public void initView() {
        uploader = new FileUpload(new Command() {
            @Override
            public void execute() {
                definitionUploadForm.submit();
            }
        } );
        uploader.setName( "definitionUploadForm" );
        fileFieldSet.add(uploader);

        /*
         * After upgrade of GWT-BOOTSTRAP3 version, will be needed to register
         * org.gwtbootstrap3.client.ui.Form.SubmitHandler
         */
        definitionUploadForm.addHandler(new FormPanel.SubmitHandler() {
            @Override
            public void onSubmit(com.google.gwt.user.client.ui.FormPanel.SubmitEvent submitEvent) {
                presenter.handleSubmit(submitEvent);
            }
        } , FormPanel.SubmitEvent.getType());

        definitionUploadForm.addSubmitCompleteHandler( new Form.SubmitCompleteHandler() {
            public void onSubmitComplete( final Form.SubmitCompleteEvent event ) {
                presenter.handleSubmitComplete( event );
            }
        } );
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;

        definitionUploadForm.setAction( getWebContext() + "/servicedefinition" );
        definitionUploadForm.setEncoding( FormPanel.ENCODING_MULTIPART );
        definitionUploadForm.setMethod( FormPanel.METHOD_POST );
        definitionUploadForm.setType( FormType.HORIZONTAL );
        definitionUploadForm.add(new Hidden("packageName", projectContext.getActivePackage().getPackageMainResourcesPath().toURI()));
    }

    @Override
    public String getFileName() {
        return uploader.getFilename();
    }

    @Override
    public void showSelectFileUploadWarning() {
        notification.fire(new NotificationEvent("Select file to upload.", NotificationEvent.NotificationType.WARNING));
    }

    @Override
    public void showUnsupportedFileTypeWarning() {
        notification.fire(new NotificationEvent("File has to have extension swagger.", NotificationEvent.NotificationType.WARNING));
    }

    @Override
    public void showUploadingResult(String message) {
        results.add(new Text(message));
    }

    @Override
    public void showUploadingBusy() {
        BusyPopup.showMessage("Uploading");
    }

    @Override
    public void hideUploadingBusy() {
        BusyPopup.close();
    }

    private String getWebContext() {
        String context = GWT.getModuleBaseURL().replace( GWT.getModuleName() + "/", "" );
        if ( context.endsWith( "/" ) ) {
            context = context.substring( 0, context.length() - 1 );
        }
        return context;
    }
}
