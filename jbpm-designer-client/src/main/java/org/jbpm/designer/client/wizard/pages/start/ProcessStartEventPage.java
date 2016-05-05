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
package org.jbpm.designer.client.wizard.pages.start;


import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.model.StandardEvent;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Date;

@Dependent
public class ProcessStartEventPage implements WizardPage, ProcessStartEventPageView.Presenter {

    private WizardPageStatusChangeEvent pageChanged = new WizardPageStatusChangeEvent(this);

    private DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm");
    private RegExp cronRegExp = RegExp.compile("^([1-9]+[0-9]*[d])?([ ])*([1-9]+[0-9]*[h])?([ ])*([1-9]+[0-9]*[m])?([ ])*([1-9]+[0-9]*[s])?([ ])*([1-9]+[0-9]*[m][s])?$");
    private RegExp signalRegExp = RegExp.compile("^[a-zA-Z0-9\\-\\.\\_]*$");

    @Inject
    Event<WizardPageStatusChangeEvent> event;

    @Inject
    ProcessStartEventPageView view;

    @Override
    public String getTitle() {
        return DesignerEditorConstants.INSTANCE.processStartEvent();
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {

        callback.callback(isStartValid());
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
    public boolean isStartValid() {

        if(view.isSelectedDateStart()) {
            String value = view.getDefinedTimeValue();
            if(value != null && !value.trim().isEmpty()) {
                value = value.trim();
                Date date = null;
                try {
                    date = dateFormat.parse(value);
                } catch (Exception e) {
                    return false;
                }
                if(date == null) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if(view.isSelectedDelayStart() || view.isSelectedCycleStart()) {
            String value = view.getDefinedTimeValue();
            if(value != null && !value.trim().isEmpty()) {
                value = value.trim();
                if (!cronRegExp.test(value)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if(view.isSelectedSignalStart()) {
            String value = view.getDefinedSignal();
            if(value!= null && !value.trim().isEmpty()) {
                value = value.trim();
                if(!signalRegExp.test(value)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void firePageChangedEvent() {
        event.fire(pageChanged);
    }

    public StandardEvent getStartEvent() {
        return view.getDefinedEvent();
    }
}
