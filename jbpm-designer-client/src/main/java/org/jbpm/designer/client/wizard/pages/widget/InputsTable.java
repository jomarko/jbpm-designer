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

import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.model.Variable;

import java.util.ArrayList;
import java.util.List;

public class InputsTable extends DeletableFlexTable {

    @Override
    public List<Widget> getNewRowWidgets() {
        List<Widget> newWidgets = new ArrayList<Widget>();
        newWidgets.add(new InputRow());
        return newWidgets;
    }


    public List<Variable> getModels() {
        List<Variable> result = new ArrayList<Variable>();
        for(int row = 0; row < container.getRowCount(); row++) {
            Widget widget = container.getWidget(row, 0);
            if(widget != null && widget instanceof InputRow) {
                result.add(((InputRow)widget).getModel());
            }
        }
        return result;
    }
}
