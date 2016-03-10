package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.client.shared.*;

import java.util.List;


public interface ProcessTasksPageView extends IsWidget {
    interface Presenter {
        Task getDefaultModel();

        boolean isRowParallel(int row);

        boolean isRowCondition(int row);

        void rowDeleted();

        void rowSelected();

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

    List<Task> getTasks();

    List<Task> getTasks(int row);

    String getRowType(int row);

    void splitRow(int row);

    int mergeRows(List<Integer> rows);

    void setRowType(int row, String type);

    void setAvailableHumanParticipants(List<User> users);

    void setAvailableGroupParticipants(List<Group> groups);

    List<Integer> getSelectedRows();

    void showHumanSpecificDetails();

    void showServiceSpecificDetails();

    void deselectAllRows();

    void highlightSelectedRows();

    void unbindAllWidgets();

    void rebindSelectedWidget();

    void setModelForSelectedWidget(Task model);

    void setAvailableVarsForSelectedTask(List<Variable> variables);

    Task getModelOfSelectedWidget();

    void setConditionModel(Condition condition);

    void showConditionAsPositive();

    void showConditionAsNegative();

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
}
