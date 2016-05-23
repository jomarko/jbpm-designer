package org.jbpm.designer.client.wizard.pages.service;


import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Form;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;

public interface ServicesPageView extends IsWidget {

    interface Presenter {
        void handleSubmit(com.google.gwt.user.client.ui.FormPanel.SubmitEvent submitEvent);
        void handleSubmitComplete(Form.SubmitCompleteEvent submitCompleteEvent);
    }

    void init(Presenter presenter);

    String getFileName();

    void showSelectFileUploadWarning();

    void showUnsupportedFileTypeWarning();

    void showUploadingBusy();

    void hideUploadingBusy();

    void showUploadingResult(ServiceUploadResultEntry entry);

    void clearUploadResults();

    void showErrorReadingPath(String messsage);

    void showWhiteSpaceDisallowedWarning();
}
