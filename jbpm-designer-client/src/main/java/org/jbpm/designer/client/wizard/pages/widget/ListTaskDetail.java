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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.shared.Task;


public class ListTaskDetail extends Composite implements HasModel<Task> {

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    private VerticalPanel line = new VerticalPanel();

    private TextBox name = new TextBox();

    private Label indicator = new Label();

    public ListTaskDetail() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(indicator);
        panel.add(name);
        name.setReadOnly(true);
        line.add(panel);
        initWidget(panel);
        dataBinder.bind(name, "name");
        dataBinder.addPropertyChangeHandler("taskType", new PropertyChangeHandler<String>() {
            @Override
            public void onPropertyChange(PropertyChangeEvent<String> propertyChangeEvent) {
                if(propertyChangeEvent.getNewValue().equals("Human")) {
                    indicator.setText("human");
                } else if(propertyChangeEvent.getNewValue().equals("Service")) {
                    indicator.setText("service");
                }
            }
        });

        setStyleName("cellWithMargin");
    }

    @Override
    public Task getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Task task) {
        dataBinder.setModel(task);
    }

    public void add(ListTaskDetail detail) {
        line.add(detail);
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void rebind() {
        dataBinder.bind(name, "name");
    }
}
