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
import org.jbpm.designer.client.wizard.pages.widget.TasksHolder;
import org.jbpm.designer.model.*;

import java.util.List;


public interface ProcessTasksPageView extends IsWidget {
    interface Presenter {
        Task getDefaultModel();

        void rowDeleted();

        void taskDetailSelected(ListTaskDetail detail);

        void holderSelected(TasksHolder holder);

        void startSelection();

        void cancelSelection();

        void splitTasks();

        void mergeTasksCondition();

        void mergeTasksParallel();

        boolean isTaskValid(Task task);

        boolean isConstraintValid(Constraint constraint);

        void firePageChangedEvent();
    }

    void init(Presenter presenter);

    List<Task> getTasks(int row);

    int getRowsCount();

    void mergeParallel(List<ListTaskDetail> widgets);

    void mergeCondition(List<ListTaskDetail> widgets);

    void split(TasksHolder holder);

    void setAvailableHumanParticipants(List<User> users);

    void setAvailableGroupParticipants(List<Group> groups);

    List<Widget> getSelectedWidgets();

    void showHumanSpecificDetails();

    void showServiceSpecificDetails();

    void deselectAll();

    void highlightSelected();

    void unbindAllWidgets();

    void rebindSelectedWidget();

    void setModelForSelectedWidget(Task model);

    void setAvailableVarsForSelectedTask(List<Variable> variables);

    void showMergeInvalidCount();

    void showAlreadyContainsMerged();

    void showSplitInvalidCount();

    void showAsValid(int taskId);

    void showAsInvalid(int taskId);

    void showButtonsAfterSelection();

    void showButtonsAfterSelectionCancel();

    void setNameHelpVisibility(boolean value);

    void setParticipantHelpVisibility(boolean value);

    void setOperationHelpVisibility(boolean value);

    void setVariableHelpVisibility(boolean value);

    void setConstraintValueHelpVisibility(boolean value);

    void setConstraintHelpVisibility(boolean value);

    void setSelectedTaskInputs(List<Variable> variables);

    void setSelectedTaskOutput(Variable variable);

    void setSplitButtonVisibility(boolean value);

    void selectAllWidgetsOfHolder(TasksHolder holder);
}
