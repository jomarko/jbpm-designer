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
                if(widget instanceof ListTaskDetail) {
                    result.add(((ListTaskDetail) widget).getModel());
                }
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

    public void split(TasksHolder holder) {
        for(int i = 1; i < holder.getTasks().size(); i++) {
            int newRow = container.insertRow(getRowOfWidget(holder));
            container.setWidget(newRow, 0, holder.getTasks().get(i));
            container.setWidget(newRow, 1, getDeleteButton());
        }

        container.setWidget(getRowOfWidget(holder), 0, holder.getTasks().get(0));
    }

    public void highlightWidgets(List<Widget> widgets) {
        for(int row = 0; row < container.getRowCount(); row++) {
            for (int column = 0; column < container.getCellCount(row); column++) {
                container.getCellFormatter().removeStyleName(row, column, "selectedRow");
                container.getCellFormatter().removeStyleName(row, column, "redRow");

                Widget widget = container.getWidget(row, column);
                if (widgets.contains(widget)) {
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

    public void setWidget(int row, int column, Widget widget) {
        container.setWidget(row, column, widget);
    }
}
