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

import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.shared.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksTable extends DeletableFlexTable<ListTaskDetail, Task> {

    @Override
    public ListTaskDetail getNewRowWidget() {
        return new ListTaskDetail();
    }

    @Override
    public List<Task> getModels() {
        List<Task> result = new ArrayList<Task>();
        for(int row = 0; row < container.getRowCount(); row++) {
            result.addAll(getRowModels(row));
        }
        return result;
    }

    public List<Task> getRowModels(int row) {
        List<Task> result = new ArrayList<Task>();
        for(int i = 0; i < container.getCellCount(row) - 1; i++) {
            Widget widget = container.getWidget(row, i);
            if(widget != null) {
                result.add(((ListTaskDetail)widget).getModel());
            }
        }
        return result;
    }

    public List<ListTaskDetail> getRowWidgets(int row) {
        List<ListTaskDetail> result = new ArrayList<ListTaskDetail>();
        for(int i = 0; i < container.getCellCount(row) - 1; i++) {
            Widget widget = container.getWidget(row, i);
            if(widget != null) {
                result.add((ListTaskDetail)widget);
            }
        }
        return result;
    }

    public int getRowCount() {
        return container.getRowCount();
    }

    public int merge(List<Integer> rows) {
        Collections.sort(rows);

        int firstRow = rows.get(0);

        for(int i = 1; i < rows.size(); i++) {
            for (int cell = 0; cell < container.getCellCount(rows.get(i)) - 1; cell++) {
                container.insertCell(firstRow, container.getCellCount(firstRow) - 1);
                container.setWidget(firstRow, container.getCellCount(firstRow) - 2, container.getWidget(rows.get(i), cell));
            }
        }

        for(int i = 1; i < rows.size(); i++) {
            container.removeRow(rows.get(i) - i + 1);
        }

        return firstRow;
    }

    public void split(int row) {
        List<Widget> widgets = new ArrayList<Widget>();
        for(int i = 1; i < container.getCellCount(row) - 1; i++) {
            widgets.add(container.getWidget(row, 1));
            container.removeCell(row, 1);
        }

        for(Widget widget : widgets) {
            int newRow = container.insertRow(row);
            container.setWidget(newRow, 0, widget);
            container.setWidget(newRow, 1, getDeleteButton(widget));
        }

    }

    public String getRowId(int row) {
        return container.getRowFormatter().getElement(row).getId();
    }

    public void setRowId(int row, String id) {
        container.getRowFormatter().getElement(row).setId(id);
    }

    public void highlightRows(List<Integer> rows) {
        for(int i = 0; i < container.getRowCount(); i++) {
            if(!rows.contains(i)) {
                if(container.getRowFormatter().getStyleName(i).contains("selectedRow")) {
                    container.getRowFormatter().removeStyleName(i, "selectedRow");
                }
            } else {
                if(!container.getRowFormatter().getStyleName(i).contains("selectedRow")) {
                    container.getRowFormatter().addStyleName(i, "selectedRow");
                }
            }
        }
    }
}
