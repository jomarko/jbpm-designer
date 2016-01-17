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

package org.jbpm.designer.client.wizard;

import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.wizard.pages.*;
import org.jbpm.designer.client.wizard.pages.inputs.ProcessInputsPage;
import org.jbpm.designer.client.wizard.pages.participants.ProcessParticipantsPage;
import org.jbpm.designer.client.wizard.pages.preview.ProcessPreviewPage;
import org.jbpm.designer.client.wizard.pages.start.ProcessStartEventPage;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPage;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.mvp.PlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class GuidedProcessWizard extends AbstractWizard {

    @Inject
    GeneralProcessInfoPage generalInfoPage;

    @Inject
    ProcessInputsPage inputsPage;

    @Inject
    ProcessParticipantsPage participantsPage;

    @Inject
    ProcessStartEventPage startEventPage;

    @Inject
    ProcessTasksPage tasksPage;

    @Inject
    ProcessPreviewPage previewPage;

    private List<WizardPage> pages;

    @PostConstruct
    public void setupPages() {
        pages = new ArrayList<WizardPage>();
        pages.add( generalInfoPage );
        pages.add( inputsPage );
        pages.add( participantsPage );
        pages.add( startEventPage );
        pages.add( tasksPage );
        pages.add( previewPage );
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget(int i) {
        return pages.get(i).asWidget();
    }

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.guidedBusinessProcess();
    }

    @Override
    public int getPreferredHeight() {
        return 860;
    }

    @Override
    public int getPreferredWidth() {
        return 1024;
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        callback.callback(true);
    }

    public void setPlace(PlaceRequest place) {
        previewPage.setPlace(place);
    }

    @Override
    public void start() {
        super.start();
        previewPage.initialise();
    }
}
