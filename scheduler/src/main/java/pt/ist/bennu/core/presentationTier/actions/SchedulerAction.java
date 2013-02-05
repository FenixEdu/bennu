/*
 * @(#)SchedulerAction.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Scheduler Module.
 *
 *   The Scheduler Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Scheduler Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Scheduler Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.core.presentationTier.actions;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.scheduler.ClassBean;
import pt.ist.bennu.core.domain.scheduler.ClassBean.Executer;
import pt.ist.bennu.core.domain.scheduler.CustomTaskLog;
import pt.ist.bennu.core.domain.scheduler.PendingExecutionTaskQueue;
import pt.ist.bennu.core.domain.scheduler.Task;
import pt.ist.bennu.core.domain.scheduler.TaskConfiguration;
import pt.ist.bennu.core.domain.scheduler.TaskConfigurationBean;
import pt.ist.bennu.core.domain.scheduler.TaskLog;
import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.CustomTaskLogAggregate;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

@Mapping(path = "/scheduler")
/**
 * 
 * @author Luis Cruz
 * 
 */
public class SchedulerAction extends ContextBaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        final User user = UserView.getCurrentUser();
        if (user == null || !user.hasRoleType(RoleType.MANAGER)) {
            throw new Error("unauthorized.access");
        }
        return super.execute(mapping, form, request, response);
    }

    public final String SCHEDULER_URL = "/scheduler.do?method=viewScheduler";
    public final String TASK_URL = "/scheduler.do?method=viewTask";

    public ActionForward viewScheduler(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final Context context = getContext(request);
        request.setAttribute("executingTasks", PendingExecutionTaskQueue.getPendingExecutionTasks());
        request.setAttribute("activeTasks", Task.getTasksSortedByLocalizedName(true));
        request.setAttribute("inactiveTasks", Task.getTasksSortedByLocalizedName(false));
        return context.forward("/bennu-core/scheduler.jsp");
    }

    public ActionForward forwardToViewScheduler(HttpServletRequest request) {

        ActionForward forward = new ActionForward();
        forward.setRedirect(true);
        String realPath = SCHEDULER_URL + "&" + CONTEXT_PATH + "=" + getContext(request).getPath();
        forward.setPath(realPath + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "="
                + GenericChecksumRewriter.calculateChecksum(request.getContextPath() + realPath));
        return forward;
    }

    public ActionForward prepareAddTaskConfiguration(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        final Context context = getContext(request);
        final Task task = getDomainObject(request, "taskId");
        TaskConfigurationBean taskConfigurationBean = getRenderedObject();
        if (taskConfigurationBean == null) {
            taskConfigurationBean = new TaskConfigurationBean(task);
        }
        request.setAttribute("taskConfigurationBean", taskConfigurationBean);
        return context.forward("/bennu-core/addTaskConfiguration.jsp");
    }

    public ActionForward addTaskConfiguration(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        final TaskConfigurationBean taskConfigurationBean = getRenderedObject();
        taskConfigurationBean.create();
        return forwardToViewScheduler(request);
    }

    public ActionForward runNow(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final Task task = getDomainObject(request, "taskId");
        task.invokeNow();

        return forwardToViewScheduler(request);
    }

    public ActionForward stopRunning(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final Task task = getDomainObject(request, "taskId");
        task.stop();

        return forwardToViewScheduler(request);
    }

    public ActionForward viewTask(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final Context context = getContext(request);
        final Task task = getDomainObject(request, "taskId");
        request.setAttribute("task", task);
        return context.forward("/bennu-core/viewTask.jsp");
    }

    public ActionForward forwardToViewTask(Task task, HttpServletRequest request) {

        ActionForward forward = new ActionForward();
        forward.setRedirect(true);
        String realPath =
                TASK_URL + "&" + "taskId=" + task.getExternalId() + "&" + CONTEXT_PATH + "=" + getContext(request).getPath();
        forward.setPath(realPath + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "="
                + GenericChecksumRewriter.calculateChecksum(request.getContextPath() + realPath));
        return forward;
    }

    public ActionForward deleteTaskConfiguration(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        final TaskConfiguration taskConfiguration = getDomainObject(request, "taskConfigurationId");
        Task task = taskConfiguration.getTask();
        taskConfiguration.delete();
        return task.getTaskConfigurationsCount() == 0 ? forwardToViewScheduler(request) : forwardToViewTask(task, request);
    }

    public ActionForward viewTaskLog(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final Context context = getContext(request);
        final TaskLog taskLog = getDomainObject(request, "taskLogId");
        request.setAttribute("taskLog", taskLog);
        return context.forward("/bennu-core/viewTaskLog.jsp");
    }

    public ActionForward prepareLoadAndRun(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        ClassBean classBean = getRenderedObject();
        if (classBean == null) {
            classBean = new ClassBean();
        }
        request.setAttribute("classBean", classBean);
        return forward(request, "/bennu-core/loadAndRun.jsp");
    }

    public ActionForward loadAndRun(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final ClassBean classBean = getRenderedObject();
        classBean.run();
        request.setAttribute("classBean", classBean);
        return redirect(request, "/scheduler.do?method=listCustomTaskLogs");
    }

    public ActionForward listCustomTaskLogs(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final List<Executer> runningExecuters = ClassBean.getRunningExecuters();
        request.setAttribute("runningExecuters", runningExecuters);

        final Set<CustomTaskLogAggregate> customTaskLogAggregates =
                new TreeSet<CustomTaskLogAggregate>(CustomTaskLogAggregate.COMPARATOR_BY_LAST_UPLOAD_DATE_AND_CLASS_NAME);
        final Set<String> visitedClassNames = new HashSet<String>();
        for (CustomTaskLog taskLog : CustomTaskLog.getSortedCustomTaskLogs()) {
            String taskClassName = taskLog.getClassName();
            if (taskClassName == null) {
                taskClassName = "";
            }

            if (visitedClassNames.contains(taskClassName)) {
                continue;
            }
            visitedClassNames.add(taskClassName);
            customTaskLogAggregates.add(new CustomTaskLogAggregate(taskClassName));
        }

        request.setAttribute("customTaskLogAggregates", customTaskLogAggregates);
        return forward(request, "/bennu-core/listCustomTaskLogs.jsp");
    }

    public ActionForward searchCustomTaskLogs(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        String className = getAttribute(request, "className");
        final CustomTaskLogAggregate aggregate = new CustomTaskLogAggregate(className);
        request.setAttribute("customTaskLogs", aggregate.getCustomTaskLogs());
        request.setAttribute("className", className);
        return forward(request, "/bennu-core/searchCustomTaskLogs.jsp");
    }

    public ActionForward deleteCustomTaskLogs(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        String className = getAttribute(request, "className");
        final CustomTaskLogAggregate aggregate = new CustomTaskLogAggregate(className);
        aggregate.deleteCustomTaskLogs();

        return listCustomTaskLogs(mapping, form, request, response);
    }

    public ActionForward viewCustomTaskLog(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final CustomTaskLog customTaskLog = getDomainObject(request, "customTaskLogId");
        request.setAttribute("customTaskLog", customTaskLog);
        return forward(request, "/bennu-core/viewCustomTaskLog.jsp");
    }

    public ActionForward reloadCustomTask(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        final CustomTaskLog customTaskLog = getDomainObject(request, "customTaskLogId");

        ClassBean classBean = new ClassBean();
        classBean.setClassName(customTaskLog.getClassName());
        classBean.setContents(customTaskLog.getContents());
        request.setAttribute("classBean", classBean);

        return forward(request, "/bennu-core/loadAndRun.jsp");
    }

    public ActionForward downloadOutputFile(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        final GenericFile genericFile = getDomainObject(request, "fileId");
        response.setContentType(genericFile.getContentType());
        response.setHeader("Content-disposition", "attachment; filename=" + genericFile.getFilename());

        final ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(genericFile.getContent());
        outputStream.flush();
        outputStream.close();

        return null;
    }

}
