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
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.util.DefaultValues;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.service.DiscoverService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.workbench.events.NotificationEvent;

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

    @Inject
    Caller<DiscoverService> discoverService;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.processInputs();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        boolean allVariablesValid = true;
        for(Variable variable : view.getInputs()) {
            if(!isVariableValid(variable)) {
                allVariablesValid = false;
                view.showAsInvalid(variable);
            } else {
                view.showAsValid(variable);
            }
        }
        view.setVariablesHelpVisibility(!allVariablesValid);
        callback.callback(allVariablesValid);
    }

    @Override
    public void initialise() {
        view.init(this);
        discoverService.call(new RemoteCallback<List<String>>() {
            @Override
            public void callback(List<String> dataTypes) {
                view.setAvailableDataTypes(dataTypes);
                event.fire(pageChanged);
            }
        }, new DefaultErrorCallback()).getExistingDataTypes();
        event.fire(pageChanged);
    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void firePageChangedEvent() {
        event.fire(pageChanged);
    }

    @Override
    public boolean isVariableValid(Variable variable) {
        boolean validationResult = true;
        if(variable == null) {
            validationResult = false;
        } else if(variable.getName() == null || variable.getName().trim().isEmpty()) {
            validationResult = false;
        } else  if(variable.getDataType() == null || variable.getDataType().trim().isEmpty()) {
            validationResult = false;
        }

        String variableName = variable.getName();
        int occurrences = 0;
        for(Variable model : view.getInputs()) {
            if(model.getName().compareTo(variableName) == 0) {
                occurrences++;
            }
        }
        if(occurrences > 1) {
            view.deleteVariable(variable);
            view.fireInputWithNameAlreadyExist();
            validationResult = false;
        }

        return validationResult;
    }

    public List<Variable> getInputs() {
        return view.getInputs();
    }
}
