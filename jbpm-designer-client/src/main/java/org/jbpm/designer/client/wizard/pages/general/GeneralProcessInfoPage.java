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

package org.jbpm.designer.client.wizard.pages.general;

import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class GeneralProcessInfoPage implements WizardPage, GeneralProcessInfoPageView.Presenter {

    private WizardPageStatusChangeEvent pageChanged = new WizardPageStatusChangeEvent(this);

    @Inject
    private Event<WizardPageStatusChangeEvent> event;

    @Inject
    private GeneralProcessInfoPageView view;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.generalProcessInfo();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        boolean isNameValid = view.getName() != null && !view.getName().isEmpty();
        if(!isNameValid) {
            view.showNameHelp();
        } else {
            view.hideNameHelp();
        }
        callback.callback(isNameValid);
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
    public void firePageChangedEvent() {
        event.fire(pageChanged);
    }

    public void setProcessName(String processName) {
        view.setName(processName);
    }

    public String getProcessName() {
        return view.getName();
    }

    public String getProcessDocumentation() {
        return view.getDescription();
    }

}
