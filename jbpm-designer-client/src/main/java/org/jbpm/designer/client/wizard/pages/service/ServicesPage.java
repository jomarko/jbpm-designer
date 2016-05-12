package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Form;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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

    @Override
    public void initialise() {
        view.init(this);
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
        String results = submitCompleteEvent.getResults();
        if(results.startsWith("Ok:") || results.startsWith("Error:")) {
            view.showUploadingResult(results);
        } else {
            view.showUploadingResult("Error: " + view.getFileName());
        }
    }
}
