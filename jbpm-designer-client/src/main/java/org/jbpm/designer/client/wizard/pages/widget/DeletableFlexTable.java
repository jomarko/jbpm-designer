/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.designer.client.wizard.pages.widget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;

import java.util.ArrayList;
import java.util.List;

public abstract class DeletableFlexTable<T extends Widget, U> extends Composite implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,MouseOutHandler {

    interface DeletableFlexTableBinder
            extends
            UiBinder<Widget, DeletableFlexTable> {
    }

    public interface RowsHandler<T> {
        void addedRow(Widget widget);

        void rowSelected(Widget widget, Integer row, boolean ctrlPressed);

        void rowDeleted();
    }

    private static DeletableFlexTableBinder uiBinder = GWT.create(DeletableFlexTableBinder.class);

    private List<RowsHandler> handlers = new ArrayList<RowsHandler>();

    @UiField
    FlexTable container;

    private boolean leftButtonDown;
    private int fromRow;
    private int toRow;

    public DeletableFlexTable() {
        initWidget(uiBinder.createAndBindUi(this));
        container.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                HTMLTable.Cell cell = container.getCellForEvent(event);
                for(RowsHandler handler : handlers) {
                    Widget widget = container.getWidget(cell.getRowIndex(), cell.getCellIndex());
                                                            ;
                    if(!(widget instanceof Button)) {
                        handler.rowSelected(widget, cell.getRowIndex(), event.getNativeEvent().getCtrlKey());
                    }
                }
            }
        });

        container.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONMOUSEMOVE);
        container.addDomHandler(this, MouseDownEvent.getType());
        container.addDomHandler(this, MouseUpEvent.getType());
        container.addDomHandler(this, MouseMoveEvent.getType());
        container.addDomHandler(this, MouseOutEvent.getType());

        container.setCellSpacing(5);
    }



    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        if(mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            leftButtonDown = true;
            fromRow = getRowOfEvent(mouseDownEvent);
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent mouseUpEvent) {
        if(leftButtonDown && mouseUpEvent.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            leftButtonDown = false;
            toRow = getRowOfEvent(mouseUpEvent);
            if(fromRow != toRow) {
                List<Widget> rowWidgets = new ArrayList<Widget>();
                for(int i = 0; i < container.getCellCount(fromRow); i++) {
                    rowWidgets.add(container.getWidget(fromRow, i));
                }
                if(fromRow < toRow) {
                    int newRow = container.insertRow(toRow + 1);
                    copyRowId(fromRow, newRow);
                    for(int i = 0; i < rowWidgets.size(); i++) {
                        container.setWidget(newRow, i, rowWidgets.get(i));
                    }
                    container.removeRow(fromRow);

                } else {
                    int newRow = container.insertRow(toRow);
                    copyRowId(fromRow + 1, newRow);
                    for(int i = 0; i < rowWidgets.size(); i++) {
                        container.setWidget(newRow, i, rowWidgets.get(i));
                    }
                    container.removeRow(fromRow + 1);
                }
            }
        }
    }

    private void copyRowId(int from, int to) {
        container.getRowFormatter().getElement(to).setId(
                container.getRowFormatter().getElement(from).getId()
        );
    }

    @Override
    public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
//        TODO - highlight row under mouse
//        if(leftButtonDown) {
//            int row = getRowOfEvent(mouseMoveEvent);
//        }
    }

    @Override
    public void onMouseOut(MouseOutEvent mouseOutEvent) {
        leftButtonDown = false;
    }

    @UiHandler("add")
    public void addHandler(ClickEvent event) {
        addNewRow(getNewRowWidget());
    }

    public abstract T getNewRowWidget();

    public abstract List<U> getModels();

    protected void addNewRow(final Widget newRowWidget) {
        int row = container.getRowCount();
        container.setWidget(row, 0, newRowWidget);
        container.setWidget(row, 1, getDeleteButton(newRowWidget));
        for(RowsHandler handler : handlers) {
            handler.addedRow(newRowWidget);
        }
    }

    public void registerRowsHandler(RowsHandler handler){
        handlers.add(handler);
    }

    private int getRowOfEvent(MouseEvent event) {
        int tableRow = 0;
        int mouseTop = event.getRelativeY(container.getElement());
        int tableTop = container.getAbsoluteTop();
        for(int row = 0; row < container.getRowCount(); row++) {
            Widget widget = container.getWidget(row, 0);
            if(widget != null) {
                int rowTop = widget.getAbsoluteTop();
                if (rowTop - tableTop > mouseTop) {
                    tableRow = row - 1;
                    break;
                }
                if (row == container.getRowCount() - 1) {
                    tableRow = row;
                }
            }
        }

        return tableRow;
    }

    public void clear(){
        container.removeAllRows();
    }

    public void setRedRowColor(int row) {
        if(!container.getRowFormatter().getStyleName(row).contains("redRow")) {
            container.getRowFormatter().addStyleName(row, "redRow");
        }
    }

    public void setNormalRowColor(int row) {
        if(container.getRowFormatter().getStyleName(row).contains("redRow")) {
            container.getRowFormatter().removeStyleName(row, "redRow");
        }
    }

    protected Button getDeleteButton(final Widget widget) {
        final Button delete = new Button("Delete");
        delete.setIcon( IconType.TRASH );
        ClickHandler deleteHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                for(int row = 0; row < container.getRowCount(); row++) {
                    if(container.getWidget(row, 0) == widget) {
                        container.removeRow(row);
                        break;
                    }
                }

                for(RowsHandler handler : handlers) {
                    handler.rowDeleted();
                }
            }
        };
        delete.addClickHandler(deleteHandler);

        return delete;
    }

}
