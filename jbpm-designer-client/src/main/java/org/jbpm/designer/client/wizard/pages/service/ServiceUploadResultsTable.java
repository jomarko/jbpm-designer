package org.jbpm.designer.client.wizard.pages.service;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.model.operation.ServiceUploadResultEntry;

import javax.inject.Inject;
import java.util.ArrayList;

@Templated("ServiceUploadResultsTable.html#widget")
public class ServiceUploadResultsTable extends Composite {

    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<ServiceUploadResultEntry, ServiceUploadResultRow> results;

    public void addEntry(ServiceUploadResultEntry entry) {
        results.getValue().add(entry);
    }

    public void clear() {
        results.setValue(new ArrayList<ServiceUploadResultEntry>(), true);
        ValueChangeEvent.fire(results, results.getValue());
    }
}
