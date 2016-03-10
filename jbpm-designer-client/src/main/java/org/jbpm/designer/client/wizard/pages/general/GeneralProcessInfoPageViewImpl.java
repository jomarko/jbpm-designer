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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;

import javax.enterprise.context.Dependent;

@Dependent
public class GeneralProcessInfoPageViewImpl extends Composite implements GeneralProcessInfoPageView {

    interface GeneralProcessInfoPageViewImplBinder
            extends
            UiBinder<Widget, GeneralProcessInfoPageViewImpl> {
    }

    private static GeneralProcessInfoPageViewImplBinder uiBinder = GWT.create(GeneralProcessInfoPageViewImplBinder.class);

    private Presenter presenter;

    @UiField
    TextBox processName;

    @UiField
    HelpBlock processNameHelp;

    @UiField
    TextArea processDescription;

    @UiField
    HelpBlock processDescriptionHelp;


    public GeneralProcessInfoPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        processName.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                presenter.firePageChangedEvent();
            }
        });
        processDescription.setText("");
    }

    @Override
    public void setName(String name) {
        processName.setText(name);
    }

    @Override
    public String getName() {
        return processName.getText();
    }

    @Override
    public String getDescription() {
        return processDescription.getText();
    }

    @Override
    public void showNameHelp() {
        processNameHelp.setVisible(true);
    }

    @Override
    public void hideNameHelp() {
        processNameHelp.setVisible(false);
    }

    @Override
    public void showDescriptionHelp() {
        processDescriptionHelp.setVisible(true);
    }
}
