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
package org.jbpm.designer.client.wizard.pages.inputs;


import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.model.Variable;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class ProcessInputsPage implements WizardPage, ProcessInputsPageView.Presenter {

    private WizardPageStatusChangeEvent pageChanged = new WizardPageStatusChangeEvent(this);

    @Inject
    private Event<WizardPageStatusChangeEvent> event;

    @Inject
    ProcessInputsPageView view;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.processInputs();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        boolean allVariablesValid = true;
        int row = 0;
        for(Variable variable : view.getInputs()) {
            if(!isVariableValid(variable)) {
                allVariablesValid = false;
                view.showAsInvalid(row);
            } else {
                view.showAsValid(row);
            }
            row++;
        }
        callback.callback(allVariablesValid);
    }

    @Override
    public void initialise() {
        view.init(this);
    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public Variable getDefaultModel() {
        Variable defaultModel = new Variable();
        defaultModel.setVariableType(Variable.VariableType.INPUT);
        defaultModel.setName("");
        defaultModel.setDataType("String");
        return defaultModel;
    }

    @Override
    public void firePageChangedEvent() {
        event.fire(pageChanged);
    }

    @Override
    public boolean isVariableValid(Variable variable) {
        if(variable == null) {
            return false;
        }
        if(variable.getName() == null || variable.getName().isEmpty()) {
            return false;
        }
        if(variable.getDataType() == null || variable.getDataType().isEmpty()) {
            return false;
        }

        return true;
    }

    public List<Variable> getInputs() {
        return view.getInputs();
    }
}
