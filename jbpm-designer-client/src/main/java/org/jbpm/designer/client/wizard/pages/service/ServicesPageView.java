package org.jbpm.designer.client.wizard.pages.service;


import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Form;

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

    void showUploadingResult(String message);
}
