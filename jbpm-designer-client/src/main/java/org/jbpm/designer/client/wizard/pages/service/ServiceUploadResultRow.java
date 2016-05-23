package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Templated("ServiceUploadResultsTable.html#result")
public class ServiceUploadResultRow extends Composite implements HasModel<ServiceUploadResultEntry> {

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @AutoBound
    protected DataBinder<ServiceUploadResultEntry> binder;

    @DataField
    Element fileNameDiv = Document.get().createElement("div");

    @DataField
    Element apiNameDiv = Document.get().createElement("div");

    @DataField
    Element versionDiv = Document.get().createElement("div");

    private Text fileName;
    private Text apiName;
    private Text version;

    @PostConstruct
    public void initialize() {
        fileName = new Text();
        fileNameDiv.appendChild(fileName.getElement());

        apiName = new Text();
        apiNameDiv.appendChild(apiName.getElement());

        version = new Text();
        versionDiv.appendChild(version.getElement());
    }

    @Override
    public ServiceUploadResultEntry getModel() {
        return binder.getModel();
    }

    @Override
    public void setModel(ServiceUploadResultEntry entry) {
        binder.setModel(entry);
        fileName.setText(entry.getFileName());
        version.setText(entry.getVersion());
        apiName.setText(entry.getApiName());
    }
}
