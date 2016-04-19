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

package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.wizard.pages.widget.ListTaskDetail;
import org.jbpm.designer.client.wizard.pages.widget.MergedTasksIndicator;
import org.jbpm.designer.model.*;

import java.util.List;
import java.util.Set;


public interface ProcessTasksPageView extends IsWidget {
    interface Presenter {
        Task getDefaultModel();

        void rowDeleted();

        void taskDetailSelected(ListTaskDetail detail);

        void splitTasks();

        void mergeTasks(boolean conditionBased);

        boolean isTaskValid(Task task);

        boolean isConstraintValid(Constraint constraint);

        void firePageChangedEvent();
    }

    void init(Presenter presenter);

    List<Task> getTasks(int row);

    List<Widget> getWidgets(int row);

    int getRowsCount();

    void mergeSelectedWidgets();

    void splitSelectedWidgets();

    void setAvailableHumanParticipants(List<User> users);

    void setAvailableGroupParticipants(List<Group> groups);

    List<Widget> getSelectedWidgets();

    void showHumanSpecificDetails();

    void showServiceSpecificDetails();

    void deselectAll();

    void highlightSelected();

    void unbindAllTaskWidgets();

    void rebindTaskDetailWidgets();

    void setModelTaskDetailWidgets(Task model);

    void rebindConditionWidgetToModel(Condition model);

    void setAvailableVarsForSelectedTask(List<Variable> variables);

    void showMergeInvalidCount();

    void showAlreadyContainsMerged();

    void showSplitInvalidCount();

    void showAsValid(int taskId);

    void showAsInvalid(int taskId);

    void setNameHelpVisibility(boolean value);

    void setParticipantHelpVisibility(boolean value);

    void setOperationHelpVisibility(boolean value);

    void setVariableHelpVisibility(boolean value);

    void setConstraintValueHelpVisibility(boolean value);

    void setConstraintHelpVisibility(boolean value);

    void setMergeButtonsVisibility(boolean value);

    void setSplitButtonVisibility(boolean value);

    void setConditionPanelVisibility(boolean value);

}
