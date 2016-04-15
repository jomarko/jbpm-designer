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
import org.jbpm.designer.client.handlers.NewProcessHandler;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.model.BusinessProcess;
import org.jbpm.designer.model.Task;
import org.jbpm.designer.model.Variable;
import org.jbpm.designer.client.wizard.pages.general.GeneralProcessInfoPage;
import org.jbpm.designer.client.wizard.pages.inputs.ProcessInputsPage;
import org.jbpm.designer.client.wizard.pages.preview.ProcessPreviewPage;
import org.jbpm.designer.client.wizard.pages.start.ProcessStartEventPage;
import org.jbpm.designer.client.wizard.pages.tasks.ProcessTasksPage;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class GuidedProcessWizard extends AbstractWizard {

    @Inject
    GeneralProcessInfoPage generalInfoPage;

    @Inject
    ProcessInputsPage inputsPage;

    @Inject
    ProcessStartEventPage startEventPage;

    @Inject
    ProcessTasksPage tasksPage;

    @Inject
    ProcessPreviewPage previewPage;

    Callback<BusinessProcess> completeProcessCallback;

    private NewProcessHandler handler;

    private List<WizardPage> pages;

    @PostConstruct
    public void setupPages() {
        pages = new ArrayList<WizardPage>();
        pages.add( generalInfoPage );
        pages.add( inputsPage );
        pages.add( startEventPage );
        pages.add( tasksPage );
        tasksPage.setWizard(this);
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget(int i) {
        WizardPage pageWidget = pages.get(i);
        pageWidget.prepareView();
        return pageWidget.asWidget();
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
        return 1280;
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback( true );

        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( Boolean.FALSE.equals( result ) ) {
                        callback.callback( false );
                    }
                }
            } );
        }
    }

    @Override
    public void start() {
        super.start();
        for(WizardPage page :pages) {
            page.initialise();
        }
    }

    @Override
    public void complete() {
        super.complete();
        completeProcessCallback.callback(constructBusinessProcess());
    }

    public void setHandler(NewProcessHandler handler) {
        this.handler = handler;
    }

    public void setProcessName(String processName){
        generalInfoPage.setProcessName(processName);
    }

    public List<Variable> getInitialInputs() {
        return inputsPage.getInputs();
    }

    public void setCompleteProcessCallback(Callback<BusinessProcess> completeProcessCallback) {
        this.completeProcessCallback = completeProcessCallback;
    }

    private BusinessProcess constructBusinessProcess() {
        BusinessProcess businessProcess = new BusinessProcess();
        businessProcess.setProcessName(generalInfoPage.getProcessName());
        businessProcess.setProcessDocumentation(generalInfoPage.getProcessDocumentation());
        businessProcess.setStartEvent(startEventPage.getStartEvent());
        List<Variable> variables = inputsPage.getInputs();
        for(Map.Entry<Integer, List<Task>> tasksGroup: tasksPage.getTasks().entrySet()) {
            for(Task task : tasksGroup.getValue()) {
                Variable taskOutput = task.getOutput();
                if(taskOutput != null && taskOutput.getName() != null &&
                    !taskOutput.getName().isEmpty() && !variables.contains(taskOutput)) {
                    variables.add(taskOutput);
                }
            }
        }
        businessProcess.setConditionBasedGroups(tasksPage.getConditionBasedGroups());
        businessProcess.setVariables(variables);
        businessProcess.setTasks(tasksPage.getTasks());
        return businessProcess;
    }

}
