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

import com.google.gwt.text.shared.Renderer;

import java.io.IOException;

public class ToStringRenderer implements Renderer {
    @Override
    public String render(Object o) {
        if(o != null && o.toString() != null) {
            return o.toString();
        } else {
            return "";
        }
    }

    @Override
    public void render(Object o, Appendable appendable) throws IOException {
        String s = render(o);
        appendable.append(s);
    }
}