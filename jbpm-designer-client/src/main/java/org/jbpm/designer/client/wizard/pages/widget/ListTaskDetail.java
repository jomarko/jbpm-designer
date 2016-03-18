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
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.model.Task;


public class ListTaskDetail extends Composite implements HasModel<Task> {

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    private TextBox name = new TextBox();

    private Icon indicator = new Icon(IconType.USER);

    public ListTaskDetail() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(indicator);
        panel.add(name);
        initWidget(panel);
        dataBinder.bind(name, "name");
        dataBinder.addPropertyChangeHandler("taskType", new PropertyChangeHandler<String>() {
            @Override
            public void onPropertyChange(PropertyChangeEvent<String> propertyChangeEvent) {
                if(propertyChangeEvent.getNewValue().equals("Human")) {
                    indicator.setType(IconType.USER);
                } else if(propertyChangeEvent.getNewValue().equals("Service")) {
                    indicator.setType(IconType.COG);
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

    public void unbind() {
        dataBinder.unbind();
    }

    public void rebind() {
        dataBinder.bind(name, "name");
    }
}
