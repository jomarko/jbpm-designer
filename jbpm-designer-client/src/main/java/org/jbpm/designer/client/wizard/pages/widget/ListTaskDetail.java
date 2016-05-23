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
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.jbpm.designer.model.Condition;
import org.jbpm.designer.model.HumanTask;
import org.jbpm.designer.model.ServiceTask;
import org.jbpm.designer.model.Task;


public class ListTaskDetail extends Composite implements HasModel<Task> {

    private DataBinder<Task> dataBinder = DataBinder.forType(Task.class);

    private DataIOEditorNameTextBox name = new DataIOEditorNameTextBox();

    private Icon indicator = new Icon(IconType.USER);

    private static int lastId = 0;

    private int id;

    private boolean initialized;

    private Condition condition;

    private boolean isMerged;

    private int isMergedWith;

    public ListTaskDetail() {
        id = ++lastId;
        initialized = false;
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(indicator);
        panel.add(name);
        initWidget(panel);
        dataBinder.bind(name, "name");
        name.setRegExp("^[a-zA-Z0-9\\-\\.\\_\\ ]*$",
                DesignerEditorConstants.INSTANCE.Removed_invalid_characters_from_name(),
                DesignerEditorConstants.INSTANCE.Invalid_character_in_name());
        setStyleName("cellWithMargin");
    }

    @Override
    public Task getModel() {
        return dataBinder.getModel();
    }

    @Override
    public void setModel(Task task) {
        dataBinder.setModel(task);
        if(task instanceof HumanTask) {
            indicator.setType(IconType.USER);
        } else if(task instanceof ServiceTask) {
            indicator.setType(IconType.COG);
        }
    }

    public void unbind() {
        dataBinder.unbind();
    }

    public void rebind() {
        dataBinder.bind(name, "name");
    }

    public int getId() {
        return id;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    public int getIsMergedWith() {
        return isMergedWith;
    }

    public void setIsMergedWith(int isMergedWith) {
        this.isMergedWith = isMergedWith;
    }
}
