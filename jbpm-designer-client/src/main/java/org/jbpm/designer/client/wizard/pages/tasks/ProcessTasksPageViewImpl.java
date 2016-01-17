package org.jbpm.designer.client.wizard.pages.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.shared.Task;
import org.jbpm.designer.client.wizard.pages.widget.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class ProcessTasksPageViewImpl extends Composite implements ProcessTasksPageView, DeletableFlexTable.RowsHandler<Task> {

    interface ProcessTasksPageViewImplBinder
            extends
            UiBinder<Widget, ProcessTasksPageViewImpl> {
    }

    private static ProcessTasksPageViewImplBinder uiBinder = GWT.create(ProcessTasksPageViewImplBinder.class);

    private Map<ListTaskDetail, Task> tasksMapper = new HashMap<ListTaskDetail, Task>();

    private Presenter presenter;

    @Inject
    public ProcessTasksPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Inject
    TaskDetail taskDetail;

    @UiField
    VerticalPanel detail;

    @UiField
    TasksTable tasksContainer;

    @UiField
    RadioButton humanTaskRadio;

    @UiField
    RadioButton serviceTaskRadio;

    @PostConstruct
    public void initView() {
        detail.add(taskDetail);
        humanTaskRadio.setValue(true);
        serviceTaskRadio.setValue(false);
        taskDetail.setVisible(true);

        tasksContainer.registerRowsHandler(this);
    }

//    @Override
//    public List<Task> getTasks() {
//        return new ArrayList<Task>(tasksMapper.values());
//    }
//
//    @Override
//    public void setTasks(List<Task> tasks) {
//        tasksMapper.clear();
//        tasksContainer.clear();
//        for(Task task : tasks) {
//            ListTaskDetail ltDetail = new ListTaskDetail();
//            ltDetail.setModel(task);
//            tasksContainer.addNewRow(ltDetail);
//        }
//    }

    @Override
    public void addedRow(WidgetWithModel<Task> widget) {
        tasksMapper.put((ListTaskDetail) widget.asWidget(), widget.getModel());
//        if(widget.getModel() instanceof Task) {
        taskDetail.setModel(tasksMapper.get(widget));
//            showHumanTask();
//        } else
//        if(widget.getModel() instanceof ServiceTask) {
//            serviceTaskDetail.setModel((ServiceTask) tasksMapper.get(widget));
//            showServiceTask();
//        }
    }

    @Override
    public void rowSelected(WidgetWithModel<Task> widget) {
//        if(widget.getModel() instanceof Task) {
            taskDetail.setModel(tasksMapper.get(widget));
//            showHumanTask();
//        } else
//        if(widget.getModel() instanceof ServiceTask) {
//            serviceTaskDetail.setModel((ServiceTask) tasksMapper.get(widget));
//            showServiceTask();
//        }
    }

    @Override
    public void rowDeleted(WidgetWithModel<Task> widget) {
        tasksMapper.remove(widget);
        taskDetail.setModel(new Task());
    }

    @UiHandler("humanTaskRadio")
    void humanRadioClicked(ClickEvent event) {
        showHumanTask();

    }

    @UiHandler("serviceTaskRadio")
    void serviceRadioClicked(ClickEvent event) {
        showServiceTask();

    }

    private void showHumanTask() {
        taskDetail.setVisible(true);

    }

    private void showServiceTask() {
        taskDetail.setVisible(false);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
