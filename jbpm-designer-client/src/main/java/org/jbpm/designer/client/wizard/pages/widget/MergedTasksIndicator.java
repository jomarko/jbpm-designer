package org.jbpm.designer.client.wizard.pages.widget;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.designer.model.Condition;


public class MergedTasksIndicator extends Icon {

    public MergedTasksIndicator() {
        setVisible(false);
        setType(IconType.CODE_FORK);
    }
}
