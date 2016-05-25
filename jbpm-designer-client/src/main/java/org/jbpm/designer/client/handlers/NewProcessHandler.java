/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.designer.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.jbpm.designer.model.BusinessProcess;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewProcessHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DesignerAssetService> designerAssetService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Bpmn2Type resourceType;

    @Inject
    private GuidedProcessWizard wizard;

    private CheckBox useWizard;

    @PostConstruct
    private void setupExtensions() {
        useWizard = new CheckBox("Use Wizard");
        extensions.add( new Pair<String, CheckBox>( "Advanced options", useWizard ) );
    }

    @Override
    public String getDescription() {
        return DesignerEditorConstants.INSTANCE.businessProcess();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
            designerAssetService.call(new RemoteCallback<Path>() {
                @Override
                public void callback(final Path path) {
                    if(!useWizard.getValue()) {
                        presenter.complete();
                        notifySuccess();
                        final PlaceRequest place = new PathPlaceRequest(path);
                        placeManager.goTo(place);
                    } else {
                        wizard.setProcessName(baseFileName.replaceAll(" ", ""));
                        wizard.setCompleteProcessCallback(completeProcessCallback(path,
                                                          pkg.getPackageMainResourcesPath(),
                                                          presenter));
                        wizard.start();
                    }
                }
            }, new DefaultErrorCallback()).createProcess(pkg.getPackageMainResourcesPath(), buildFileName(baseFileName,
                    resourceType));
    }

    private Callback<BusinessProcess> completeProcessCallback(final Path oldProcessPath,
                                                              final Path basePackage,
                                                              final NewResourcePresenter presenter) {
        return new Callback<BusinessProcess>() {
            @Override
            public void callback(BusinessProcess process) {
                designerAssetService.call(new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path path) {
                        presenter.complete();
                        notifySuccess();
                        PlaceRequest place = new PathPlaceRequest(path);
                        placeManager.goTo(place);
                    }
                }, new DefaultErrorCallback()).updateProcess(oldProcessPath, basePackage, process);
            }
        };
    }
}
