package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.wizard.pages.inputs.InputDeletedEvent;
import org.jbpm.designer.client.wizard.pages.widget.ListTaskDetail;
import org.jbpm.designer.model.*;
import org.jbpm.designer.client.wizard.GuidedProcessWizard;
import org.jbpm.designer.model.operation.Operation;
import org.jbpm.designer.model.operation.ParameterMapping;
import org.jbpm.designer.model.operation.SwaggerParameter;
import org.jbpm.designer.service.DiscoverService;
import org.jbpm.designer.service.SwaggerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessTasksPageTest {

    Event<WizardPageStatusChangeEvent> event = mock(EventSourceMock.class);

    @Mock
    GuidedProcessWizard wizard;

    @Mock
    ProcessTasksPageView view;

    @Mock
    ClientUserSystemManager manager;

    @Mock
    UserManager userManager;

    @Mock
    GroupManager groupManager;

    @Mock
    SwaggerService swaggerService;

    @Captor
    ArgumentCaptor<AbstractEntityManager.SearchRequest> requestCaptor;

    @Captor
    ArgumentCaptor<Condition> conditionCaptor;

    @Mock
    Callback<Boolean> callback;

    @Spy
    @InjectMocks
    ProcessTasksPage page = new ProcessTasksPage();

    private Variable varA;
    private Variable varB;
    private Variable varC;
    private Variable siblingVariable;

    private HumanTask taskOne;
    private ServiceTask taskTwo;
    private HumanTask taskThree;

    private List<Task> simpleRow;
    private List<Task> mergedRow;
    private List<Task> rowWithOutput;

    @Mock
    private ListTaskDetail widgetOne;
    @Mock
    private ListTaskDetail widgetTwo;

    private List<Variable> wizardInputs;

    @Before
    public void setUp() {
        page.discoverService = new CallerMock<DiscoverService>(mock(DiscoverService.class));

        varA = new Variable("a", Variable.VariableType.INPUT, "String", null);
        varB = new Variable("b", Variable.VariableType.OUTPUT, "Boolean", null);
        varC = new Variable("c", Variable.VariableType.INPUT, "String", null);
        siblingVariable = new Variable("sibling", Variable.VariableType.INPUT, "String", null);
        wizardInputs = new ArrayList<Variable>();
        wizardInputs.add(varA);
        wizardInputs.add(varB);
        when(wizard.getInitialInputs()).thenReturn(wizardInputs);

        when(widgetOne.getId()).thenReturn(1);
        when(widgetTwo.getId()).thenReturn(2);

        taskOne = new HumanTask("one");
        List<Variable> oneOutputs = new ArrayList<Variable>();
        oneOutputs.add(siblingVariable);
        taskOne.setOutputs(oneOutputs);
        taskTwo = new ServiceTask("two");
        taskThree = new HumanTask("three");
        List<Variable> threeOutputs = new ArrayList<Variable>();
        threeOutputs.add(varC);
        taskThree.setOutputs(threeOutputs);

        simpleRow = new ArrayList<Task>();
        simpleRow.add(taskOne);

        mergedRow = new ArrayList<Task>();
        mergedRow.add(taskOne);
        mergedRow.add(taskTwo);

        rowWithOutput = new ArrayList<Task>();
        rowWithOutput.add(taskThree);

        page.setWizard(wizard);

        when(manager.users(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(userManager);
        when(manager.groups(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(groupManager);
    }

    @Test
    public void testInitialise()  {
        page.initialise();
        verify(view).init(page);
        verify(view).setAvailableDataTypes(any(List.class));
        verify(event, times(2)).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testIsCompleteEmpty() {
        when(view.getRowsCount()).thenReturn(0);
        page.isComplete(callback);
        verify(callback).callback(true);
    }

    @Test
    public void testIsCompleteMissingResponsible() {
        when(view.getRowsCount()).thenReturn(1);
        when(view.getTasks(0)).thenReturn(simpleRow);
        page.isComplete(callback);
        verify(callback).callback(false);
    }

    @Test
    public void testIsCompleteResponsibleGroup() {
        when(view.getRowsCount()).thenReturn(1);
        when(view.getTasks(0)).thenReturn(simpleRow);
        taskOne.setResponsibleGroup(new Group("abc"));
        page.isComplete(callback);
        verify(callback).callback(true);
    }

    @Test
    public void testIsCompleteResponsibleHuman() {
        when(view.getRowsCount()).thenReturn(1);
        when(view.getTasks(0)).thenReturn(simpleRow);
        taskOne.setResponsibleHuman(new Participant("abc"));
        page.isComplete(callback);
        verify(callback).callback(true);
    }

    @Test
    public void testSplitInvalidCount() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.splitTasks();
        verify(view, times(1)).showSplitInvalidCount();

        selected.add(mock(ListTaskDetail.class));
        page.splitTasks();
        verify(view, times(2)).showSplitInvalidCount();

        selected.add(mock(ListTaskDetail.class));
        page.splitTasks();
        verify(view, times(2)).showSplitInvalidCount();

        selected.add(mock(ListTaskDetail.class));
        page.splitTasks();
        verify(view, times(3)).showSplitInvalidCount();
    }

    @Test
    public void testSplitTasksNotMerged() {
        List<Widget> selected = new ArrayList<Widget>();
        when(widgetOne.isMerged()).thenReturn(false);
        when(widgetTwo.isMerged()).thenReturn(true);
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.splitTasks();
        verify(view, never()).splitSelectedWidgets();
        verify(view, never()).showSplitInvalidCount();
    }

    @Test
    public void testSplitTasksMergedNotSameRow() {
        List<Widget> selected = new ArrayList<Widget>();
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetOne.getIsMergedWith()).thenReturn(2);
        when(widgetTwo.isMerged()).thenReturn(true);
        when(widgetTwo.getIsMergedWith()).thenReturn(3);
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.splitTasks();
        verify(view, never()).splitSelectedWidgets();
        verify(view, never()).showSplitInvalidCount();
    }

    @Test
    public void testSplitTasksMergedSameRow() {
        List<Widget> selected = new ArrayList<Widget>();
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetOne.getIsMergedWith()).thenReturn(2);
        when(widgetTwo.isMerged()).thenReturn(true);
        when(widgetTwo.getIsMergedWith()).thenReturn(1);
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.splitTasks();
        verify(widgetOne).setMerged(false);
        verify(widgetTwo).setMerged(false);
        verify(widgetOne).setCondition(null);
        verify(widgetTwo).setCondition(null);
        verify(view).splitSelectedWidgets();
        verify(view).setTaskPanelVisibility(false);
        verify(view).deselectAll();
        verify(view).setSplitButtonVisibility(false);
        verify(view).setConditionPanelVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
        verify(view, never()).showSplitInvalidCount();
    }

    @Test
    public void testMergeInvalidCount() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.mergeTasks(false);
        verify(view, times(1)).showMergeInvalidCount();

        selected.add(mock(ListTaskDetail.class));
        page.mergeTasks(false);
        verify(view, times(2)).showMergeInvalidCount();

        selected.add(mock(ListTaskDetail.class));
        page.mergeTasks(false);
        verify(view, times(2)).showMergeInvalidCount();

        selected.add(mock(ListTaskDetail.class));
        page.mergeTasks(false);
        verify(view, times(3)).showMergeInvalidCount();
    }

    @Test
    public void testMergeMergedAlready() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetTwo.isMerged()).thenReturn(false);
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.mergeTasks(false);
        verify(view, never()).mergeSelectedWidgets(false);
        verify(view).showAlreadyContainsMerged();
    }

    @Test
    public void testMergeNotCondition() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        when(widgetOne.isMerged()).thenReturn(false);
        when(widgetTwo.isMerged()).thenReturn(false);
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.mergeTasks(false);
        verify(widgetOne).setMerged(true);
        verify(widgetTwo).setMerged(true);
        verify(widgetOne).setIsMergedWith(2);
        verify(widgetTwo).setIsMergedWith(1);
        verify(widgetOne, never()).setCondition(any(Condition.class));
        verify(widgetTwo, never()).setCondition(any(Condition.class));
        verify(view).mergeSelectedWidgets(false);
        verify(view).deselectAll();
        verify(view).setMergeButtonsVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
        verify(view, never()).showAlreadyContainsMerged();
    }

    @Test
    public void testMergeCondition() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        when(widgetOne.isMerged()).thenReturn(false);
        when(widgetTwo.isMerged()).thenReturn(false);
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.mergeTasks(true);
        verify(widgetOne).setMerged(true);
        verify(widgetTwo).setMerged(true);
        verify(widgetOne).setIsMergedWith(2);
        verify(widgetTwo).setIsMergedWith(1);
        verify(widgetOne).setCondition(conditionCaptor.capture());
        assertNotNull(conditionCaptor.getValue());
        verify(widgetTwo).setCondition(conditionCaptor.capture());
        assertNotNull(conditionCaptor.getValue());
        verify(view).mergeSelectedWidgets(true);
        verify(view).deselectAll();
        verify(view).setMergeButtonsVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
        verify(view, never()).showAlreadyContainsMerged();
    }

    @Test
    public void testRowDeleted() throws Exception {
        page.rowDeleted();
        verify(view).deselectAll();
        verify(view).setSplitButtonVisibility(false);
        verify(view).setMergeButtonsVisibility(false);
        verify(view).setConditionPanelVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testTaskDetailSelectedConditionPanel() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(view.getSelectedWidgets()).thenReturn(selected);
        when(widgetOne.getCondition()).thenReturn(null);
        page.taskDetailSelected(widgetOne);
        verify(view).setConditionPanelVisibility(false);
        verify(view, never()).setConditionPanelVisibility(true);

        when(widgetOne.getCondition()).thenReturn(mock(Condition.class));
        page.taskDetailSelected(widgetOne);
        verify(view).setConditionPanelVisibility(false);
        verify(view).setConditionPanelVisibility(true);
        verify(event, times(2)).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testTaskDetailSelectedNoPossibleAction() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.taskDetailSelected(widgetOne);
        verify(view).setTaskPanelVisibility(false);
        verify(view).setMergeButtonsVisibility(false);
        verify(view).setSplitButtonVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testTaskDetailSelectedCanBeMerged() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        selected.add(widgetTwo);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.taskDetailSelected(widgetOne);
        verify(view).setTaskPanelVisibility(false);
        verify(view).setMergeButtonsVisibility(true);
        verify(view).setSplitButtonVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testTaskDetailSelectedSameRow() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetOne.getIsMergedWith()).thenReturn(2);
        selected.add(widgetTwo);
        when(widgetTwo.isMerged()).thenReturn(true);
        when(widgetTwo.getIsMergedWith()).thenReturn(1);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.taskDetailSelected(widgetOne);
        verify(view).setTaskPanelVisibility(false);
        verify(view).setMergeButtonsVisibility(false);
        verify(view).setSplitButtonVisibility(true);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testTaskDetailSelectedNotSameRow() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetOne.getIsMergedWith()).thenReturn(2);
        selected.add(widgetTwo);
        when(widgetTwo.isMerged()).thenReturn(true);
        when(widgetTwo.getIsMergedWith()).thenReturn(3);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.taskDetailSelected(widgetOne);
        verify(view).setTaskPanelVisibility(false);
        verify(view).setMergeButtonsVisibility(false);
        verify(view).setSplitButtonVisibility(false);
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testHumanTaskDetailSelected() throws Exception {
        HumanTask model = mock(HumanTask.class);
        when(widgetOne.getModel()).thenReturn(model);
        page.taskDetailSelected(widgetOne);
        verifyRebinding(model);
    }

    @Test
    public void testServiceTaskDetailSelected() throws Exception {
        ServiceTask model = mock(ServiceTask.class);
        when(widgetOne.getModel()).thenReturn(model);
        page.taskDetailSelected(widgetOne);
        verifyRebinding(model);
    }

    private void verifyRebinding(Task model) {
        verify(view).unbindAllTaskWidgets();
        if(model instanceof HumanTask) {
            verify(view).showHumanSpecificDetails();
            verify(view, never()).showServiceSpecificDetails();
        } else {
            verify(view, never()).showHumanSpecificDetails();
            verify(view).showServiceSpecificDetails();
        }
        verify(view).setAcceptableVariablesForInputs(anyList());
        verify(view).setAcceptableVariablesForConditions(anyList());
        verify(widgetOne).setModel(model);
        verify(view).setModelTaskDetailWidgets(model);
        verify(widgetOne).rebind();
        verify(view).rebindTaskDetailWidgets();
        verify(view).highlightSelected();
    }

    @Test
    public void testTaskDetailSelectedWithCondition() throws Exception {
        ServiceTask model = mock(ServiceTask.class);
        when(widgetOne.getModel()).thenReturn(model);
        Condition condition = mock(Condition.class);
        when(widgetOne.getCondition()).thenReturn(condition);
        page.taskDetailSelected(widgetOne);
        verifyRebinding(model);
        verify(view).rebindConditionWidgetToModel(condition);
    }

    @Test
    public void testGetVariablesForTaskOnlyInitial() {
        when(view.getRowsCount()).thenReturn(0);
        List<Variable> variables = page.getVariablesForTask(mock(Task.class));
        assertEquals(wizardInputs, variables);
    }

    @Test
    public void testGetVariablesForTaskInitialAndFromPrevious() {
        when(view.getRowsCount()).thenReturn(2);
        when(view.getTasks(0)).thenReturn(rowWithOutput);
        when(view.getTasks(1)).thenReturn(simpleRow);
        List<Variable> variables = page.getVariablesForTask(taskOne);
        assertEquals(3, variables.size());
        assertTrue(variables.contains(varA));
        assertTrue(variables.contains(varB));
        assertTrue(variables.contains(varC));
    }
    @Test
    public void testGetVariablesForTaskInitialAndNotSameGroup() {
        when(view.getRowsCount()).thenReturn(2);
        when(view.getTasks(0)).thenReturn(rowWithOutput);
        when(view.getTasks(1)).thenReturn(mergedRow);
        List<Variable> variables = page.getVariablesForTask(taskTwo);
        assertEquals(3, variables.size());
        assertTrue(variables.contains(varA));
        assertTrue(variables.contains(varB));
        assertTrue(variables.contains(varC));
    }

    @Test
    public void testIsTaskValidMissingOperation() throws Exception {
        assertFalse(page.isTaskValid(taskTwo));
    }

    @Test
    public void testIsTaskValidMissingRequiredParameter() throws Exception {
        Operation operation = new Operation();
        ParameterMapping parameterMapping = new ParameterMapping();
        parameterMapping.setRequired(true);
        operation.setParameterMappings(Arrays.asList(parameterMapping));
        taskTwo.setOperation(operation);
        assertFalse(page.isTaskValid(taskTwo));
    }

    @Test
    public void testIsTaskValidSetRequiredParameter() throws Exception {
        Operation operation = new Operation();
        ParameterMapping parameterMapping = new ParameterMapping();
        parameterMapping.setRequired(true);
        parameterMapping.setVariable(mock(Variable.class));
        operation.setParameterMappings(Arrays.asList(parameterMapping));
        taskTwo.setOperation(operation);
        assertTrue(page.isTaskValid(taskTwo));
    }

    @Test
    public void testIsTaskValidMissingParameter() throws Exception {
        Operation operation = new Operation();
        ParameterMapping parameterMapping = new ParameterMapping();
        operation.setParameterMappings(Arrays.asList(parameterMapping));
        taskTwo.setOperation(operation);
        assertTrue(page.isTaskValid(taskTwo));
    }

    @Test
    public void testIsConstrainValidNullVariable() throws Exception {
        Condition condition = mock(Condition.class);
        assertFalse(page.validateCondition(condition, false));
        verify(view, never()).setVariableHelpVisibility(anyBoolean());
    }

    @Test
    public void testIsConstrainValidNullVariableShwoHelp() throws Exception {
        Condition condition = mock(Condition.class);
        assertFalse(page.validateCondition(condition, true));
        verify(view).setVariableHelpVisibility(true);
    }

    @Test
    public void testIsConstrainValidNullCondition() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        assertFalse(page.validateCondition(condition, false));
        verify(view, never()).setVariableHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintHelpVisibility(anyBoolean());
    }

    @Test
    public void testIsConstrainValidNullConditionShowHelp() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        assertFalse(page.validateCondition(condition, true));
        verify(view).setVariableHelpVisibility(false);
        verify(view).setConstraintHelpVisibility(true);
    }

    @Test
    public void testIsConstrainValidEmptyCondition() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("  ");
        assertFalse(page.validateCondition(condition, false));
        verify(view, never()).setVariableHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintHelpVisibility(anyBoolean());
    }

    @Test
    public void testIsConstrainValidEmptyConditionShowHelp() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("  ");
        assertFalse(page.validateCondition(condition, true));
        verify(view).setVariableHelpVisibility(false);
        verify(view).setConstraintHelpVisibility(true);
    }

    @Test
    public void testIsConstrainValidNullValue() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("xxx");
        assertFalse(page.validateCondition(condition, false));
        verify(view, never()).setVariableHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintValueHelpVisibility(anyBoolean());
    }

    @Test
    public void testIsConstrainValidNullValueShowHelp() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("xxx");
        assertFalse(page.validateCondition(condition, true));
        verify(view).setVariableHelpVisibility(false);
        verify(view).setConstraintHelpVisibility(false);
        verify(view).setConstraintValueHelpVisibility(true);
    }

    @Test
    public void testIsConstrainValidEmptyValue() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("xxx");
        when(condition.getConstraintValue()).thenReturn("  ");
        assertFalse(page.validateCondition(condition, false));
        verify(view, never()).setVariableHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintValueHelpVisibility(anyBoolean());
    }

    @Test
    public void testIsConstrainValidEmptyValueShowHelp() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("xxx");
        when(condition.getConstraintValue()).thenReturn("  ");
        assertFalse(page.validateCondition(condition, true));
        verify(view).setVariableHelpVisibility(false);
        verify(view).setConstraintHelpVisibility(false);
        verify(view).setConstraintValueHelpVisibility(true);
    }

    @Test
    public void testIsConstrainValidComplete() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("xxx");
        when(condition.getConstraintValue()).thenReturn("yyy");
        assertTrue(page.validateCondition(condition, false));
        verify(view, never()).setVariableHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintHelpVisibility(anyBoolean());
        verify(view, never()).setConstraintValueHelpVisibility(anyBoolean());
    }

    @Test
    public void testIsConstrainValidCompleteShowHelp() throws Exception {
        Condition condition = mock(Condition.class);
        when(condition.getVariable()).thenReturn(mock(Variable.class));
        when(condition.getConstraint()).thenReturn("xxx");
        when(condition.getConstraintValue()).thenReturn("yyy");
        assertTrue(page.validateCondition(condition, true));
        verify(view).setVariableHelpVisibility(false);
        verify(view).setConstraintHelpVisibility(false);
        verify(view).setConstraintValueHelpVisibility(false);
    }

    @Test
    public void testMergedConditionRowsEmpty() throws Exception {
        when(view.getRowsCount()).thenReturn(0);
        Map<Integer, List<Condition>> conditions = page.getMergedRowsWithConditions();
        assertEquals(0, conditions.size());
    }

    @Test
    public void testMergedConditionNotMerged() throws Exception {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(widgetOne);
        when(view.getRowsCount()).thenReturn(1);
        when(view.getWidgets(0)).thenReturn(widgets);
        Map<Integer, List<Condition>> conditions = page.getMergedRowsWithConditions();
        assertEquals(0, conditions.size());
        verify(view).getWidgets(0);
    }

    @Test
    public void testMergedConditionMerged() throws Exception {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(widgetOne);
        widgets.add(widgetTwo);
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetTwo.isMerged()).thenReturn(true);
        when(view.getRowsCount()).thenReturn(1);
        when(view.getWidgets(0)).thenReturn(widgets);
        Map<Integer, List<Condition>> conditions = page.getMergedRowsWithConditions();
        assertEquals(0, conditions.size());
        verify(view).getWidgets(0);
    }

    @Test
    public void testMergedCondition() throws Exception {
        List<Widget> widgets = new ArrayList<Widget>();
        widgets.add(widgetOne);
        widgets.add(widgetTwo);
        when(widgetOne.isMerged()).thenReturn(true);
        when(widgetTwo.isMerged()).thenReturn(true);
        when(widgetOne.getCondition()).thenReturn(mock(Condition.class));
        when(widgetTwo.getCondition()).thenReturn(mock(Condition.class));
        when(view.getRowsCount()).thenReturn(1);
        when(view.getWidgets(0)).thenReturn(widgets);
        Map<Integer, List<Condition>> conditions = page.getMergedRowsWithConditions();
        assertEquals(1, conditions.size());
        assertEquals(2, conditions.get(0).size());
        verify(view).getWidgets(0);
    }

    @Test
    public void testShowHelpSelectedTaskMissingName() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.getModel()).thenReturn(taskOne);
        taskOne.setName("  ");
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.showHelpForSelectedTask(null);
        verify(view).setNameHelpVisibility(true);
    }

    @Test
    public void testShowHelpSelectedTaskMissingHuman() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.getModel()).thenReturn(taskOne);
        taskOne.setName("a");
        taskOne.setResponsibleGroup(new Group("g"));
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.showHelpForSelectedTask(null);
        verify(view).setNameHelpVisibility(false);
        verify(view).setParticipantHelpVisibility(false);
    }

    @Test
    public void testShowHelpSelectedTaskMissingGroup() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.getModel()).thenReturn(taskOne);
        taskOne.setName("a");
        taskOne.setResponsibleHuman(new Participant("human"));
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.showHelpForSelectedTask(null);
        verify(view).setNameHelpVisibility(false);
        verify(view).setParticipantHelpVisibility(false);
    }

    @Test
    public void testShowHelpSelectedTaskMissingGroupAndParticipant() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.getModel()).thenReturn(taskOne);
        taskOne.setName("a");
        taskOne.setResponsibleHuman(null);
        taskOne.setResponsibleGroup(null);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.showHelpForSelectedTask(null);
        verify(view).setNameHelpVisibility(false);
        verify(view).setParticipantHelpVisibility(true);
    }

    @Test
    public void testShowHelpSelectedTaskMissingOperation() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.getModel()).thenReturn(taskTwo);
        taskTwo.setName("a");
        taskTwo.setOperation(null);
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.showHelpForSelectedTask(null);
        verify(view).setNameHelpVisibility(false);
        verify(view).setOperationHelpVisibility(true);
    }

    @Test
    public void testShowHelpSelectedTaskOperation() throws Exception {
        List<Widget> selected = new ArrayList<Widget>();
        selected.add(widgetOne);
        when(widgetOne.getModel()).thenReturn(taskTwo);
        taskTwo.setName("a");
        taskTwo.setOperation(mock(Operation.class));
        when(view.getSelectedWidgets()).thenReturn(selected);
        page.showHelpForSelectedTask(null);
        verify(view).setNameHelpVisibility(false);
        verify(view).setOperationHelpVisibility(false);
    }

    @Test
    public void testRemoveNonExistingBindings() {
        InputDeletedEvent inputsChanged = new InputDeletedEvent();
        inputsChanged.setDeletedInput(varC);

        when(view.getRowsCount()).thenReturn(1);
        when(view.getTasks(0)).thenReturn(mergedRow);

        Operation operation = new Operation();
        ParameterMapping parameterMapping = new ParameterMapping();
        parameterMapping.setRequired(true);
        parameterMapping.setVariable(varC);
        parameterMapping.setParameter(mock(SwaggerParameter.class));
        operation.setParameterMappings(Arrays.asList(parameterMapping));
        taskTwo.setOperation(operation);

        Map<String, Variable> taskInputs = new HashMap<String, Variable>();
        taskInputs.put("aaa", varC);
        taskTwo.setInputs(taskInputs);

        assertEquals(varC, taskTwo.getOperation().getParameterMappings().get(0).getVariable());

        page.removeNonExistingBindings(inputsChanged);

        assertEquals(0, taskTwo.getInputs().size());
        assertEquals(null, taskTwo.getOperation().getParameterMappings().get(0).getVariable());
        verify(event).fire(any(WizardPageStatusChangeEvent.class));
    }
}
