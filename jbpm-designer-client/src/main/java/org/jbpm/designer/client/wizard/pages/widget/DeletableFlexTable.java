package org.jbpm.designer.client.wizard.pages.widget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;

import java.util.ArrayList;
import java.util.List;

public abstract class DeletableFlexTable<T> extends Composite {

    interface DeletableFlexTableBinder
            extends
            UiBinder<Widget, DeletableFlexTable> {
    }

    public interface RowsHandler<T> {
        void addedRow(WidgetWithModel<T> widget);

        void rowSelected(WidgetWithModel<T> widget);

        void rowDeleted(WidgetWithModel<T> widget);
    }

    private static DeletableFlexTableBinder uiBinder = GWT.create(DeletableFlexTableBinder.class);

    public DeletableFlexTable() {
        initWidget(uiBinder.createAndBindUi(this));
        container.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                HTMLTable.Cell cell = container.getCellForEvent(event);
                for(RowsHandler<T> handler : handlers) {
                    handler.rowSelected((WidgetWithModel<T>) container.getWidget(cell.getRowIndex(), 0));
                }
            }
        });
    }

    private List<RowsHandler<T>> handlers = new ArrayList<RowsHandler<T>>();

    @UiField
    FlexTable container;

    @UiHandler("add")
    public void addHandler(ClickEvent event) {
        addNewRow(getNewRowWidget());
    }

    public abstract WidgetWithModel<T> getNewRowWidget();

    public void addNewRow(final WidgetWithModel<T> newRowWidget) {
        final Button delete = new Button("Delete");
        ClickHandler deleteHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                container.remove(newRowWidget);
                container.remove(delete);

                for(RowsHandler<T> handler : handlers) {
                    handler.rowDeleted(newRowWidget);
                }
            }
        };
        delete.addClickHandler(deleteHandler);
        int row = container.getRowCount();
        container.setWidget(row, 0, newRowWidget);
        container.setWidget(row, 1, delete);

        for(RowsHandler<T> handler : handlers) {
            handler.addedRow(newRowWidget);
        }
    }

    public void registerRowsHandler(RowsHandler<T> handler){
        handlers.add(handler);
    }
}
