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
import org.jbpm.designer.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksTable extends DeletableFlexTable {

    @Override
    public List<Widget> getNewRowWidgets() {
        List<Widget> newWidgets = new ArrayList<Widget>();
        newWidgets.add(new MergedTasksIndicator());
        newWidgets.add(new ListTaskDetail());
        return newWidgets;
    }

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
            if(widget != null && widget instanceof ListTaskDetail) {
                result.add(((ListTaskDetail) widget).getModel());
            }
        }
        return result;
    }

    public List<Widget> getRowWidgets(int row) {
        List<Widget> result = new ArrayList<Widget>();
        for(int i = 0; i < container.getCellCount(row) - 1; i++) {
            Widget widget = container.getWidget(row, i);
            if(widget != null) {
                result.add(widget);
            }
        }
        return result;
    }

    public int getRowCount() {
        return container.getRowCount();
    }

    public void split(int row) {
        List<Widget> widgetsToMove = new ArrayList<Widget>();
        for(int column = 2; column < container.getCellCount(row) - 1; column++) {
            widgetsToMove.add(container.getWidget(row, 2));
            container.removeCell(row, 2);
        }

        if(container.getWidget(row, 0) instanceof MergedTasksIndicator) {
            MergedTasksIndicator indicator = (MergedTasksIndicator) container.getWidget(row, 0);
            indicator.setVisible(false);
        }

        for(Widget widget : widgetsToMove) {
            int newRow = container.insertRow(row);
            List<Widget> newWidgets = new ArrayList<Widget>();
            newWidgets.add(new MergedTasksIndicator());
            newWidgets.add(widget);
            addNewRow(newRow, newWidgets);
        }
    }

    public void highlightWidgets(List<Widget> widgets) {
        for(int row = 0; row < container.getRowCount(); row++) {
            for (int column = 0; column < container.getCellCount(row); column++) {
                container.getCellFormatter().removeStyleName(row, column, "selectedRow");
                container.getCellFormatter().removeStyleName(row, column, "redRow");

                if (widgets.contains(container.getWidget(row, column))) {
                    container.getCellFormatter().addStyleName(row, column, "selectedRow");
                }
            }
        }
    }

    public int getRowOfWidget(Widget widget) {
        for(int row = 0; row < container.getRowCount(); row++) {
            for (int column = 0; column < container.getCellCount(row); column++) {

                if(widget == container.getWidget(row, column)) {
                    return row;
                }
            }
        }

        return -1;
    }

    public void removeRow(int row) {
        container.removeRow(row);
    }

    public void addWidgetToEnd(int row, Widget widget) {
        container.insertCell(row, container.getCellCount(row) - 1);
        container.setWidget(row, container.getCellCount(row) - 2, widget);
    }
}
