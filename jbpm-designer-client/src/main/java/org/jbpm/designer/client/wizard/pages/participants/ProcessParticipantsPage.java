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
package org.jbpm.designer.client.wizard.pages.participants;


import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ProcessParticipantsPage implements WizardPage {

    @Inject
    ProcessParticipantsPageView view;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.processParticipants();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {

    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
