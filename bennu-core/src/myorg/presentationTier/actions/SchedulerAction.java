package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.scheduler.Task;
import myorg.domain.scheduler.TaskConfiguration;
import myorg.domain.scheduler.TaskConfigurationBean;
import myorg.domain.scheduler.TaskLog;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/scheduler" )
public class SchedulerAction extends ContextBaseAction {

    public ActionForward viewScheduler(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	request.setAttribute("activeTasks", Task.getTasksSortedByLocalizedName(true));
	request.setAttribute("inactiveTasks", Task.getTasksSortedByLocalizedName(false));
	return context.forward("/myorg/scheduler.jsp");
    }

    public ActionForward prepareAddTaskConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Task task = getDomainObject(request, "taskId");
	TaskConfigurationBean taskConfigurationBean = getRenderedObject();
	if (taskConfigurationBean == null) {
	    taskConfigurationBean = new TaskConfigurationBean(task);
	}
	request.setAttribute("taskConfigurationBean", taskConfigurationBean);
	return context.forward("/myorg/addTaskConfiguration.jsp");
    }

    public ActionForward addTaskConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final TaskConfigurationBean taskConfigurationBean = getRenderedObject();
	taskConfigurationBean.create();
	return viewScheduler(mapping, form, request, response);
    }

    public ActionForward viewTask(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Task task = getDomainObject(request, "taskId");
	request.setAttribute("task", task);
	return context.forward("/myorg/viewTask.jsp");
    }

    public ActionForward deleteTaskConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final TaskConfiguration taskConfiguration = getDomainObject(request, "taskConfigurationId");
	taskConfiguration.delete();
	return viewScheduler(mapping, form, request, response);
    }

    public ActionForward viewTaskLog(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final TaskLog taskLog = getDomainObject(request, "taskLogId");
	request.setAttribute("taskLog", taskLog);
	return context.forward("/myorg/viewTaskLog.jsp");
    }

}
