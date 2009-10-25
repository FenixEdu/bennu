/*
 * @(#)SchedulerAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.scheduler.ClassBean;
import myorg.domain.scheduler.Task;
import myorg.domain.scheduler.TaskConfiguration;
import myorg.domain.scheduler.TaskConfigurationBean;
import myorg.domain.scheduler.TaskLog;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/scheduler")
public class SchedulerAction extends ContextBaseAction {

    public ActionForward viewScheduler(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	request.setAttribute("activeTasks", Task.getTasksSortedByLocalizedName(true));
	request.setAttribute("inactiveTasks", Task.getTasksSortedByLocalizedName(false));
	return context.forward("/myorg/scheduler.jsp");
    }

    public ActionForward prepareAddTaskConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Task task = getDomainObject(request, "taskId");
	TaskConfigurationBean taskConfigurationBean = getRenderedObject();
	if (taskConfigurationBean == null) {
	    taskConfigurationBean = new TaskConfigurationBean(task);
	}
	request.setAttribute("taskConfigurationBean", taskConfigurationBean);
	return context.forward("/myorg/addTaskConfiguration.jsp");
    }

    public ActionForward addTaskConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final TaskConfigurationBean taskConfigurationBean = getRenderedObject();
	taskConfigurationBean.create();
	return viewScheduler(mapping, form, request, response);
    }
    
    public ActionForward runNow(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Task task = getDomainObject(request, "taskId");
	TaskConfigurationBean taskConfigurationBean =new TaskConfigurationBean(task);
	DateTime now = new DateTime().plusMinutes(1);
	taskConfigurationBean.setMinute(now.getMinuteOfHour());
	taskConfigurationBean.setHour(now.getHourOfDay());
	taskConfigurationBean.setDay(now.getDayOfMonth());
	taskConfigurationBean.setMonth(now.getMonthOfYear());
	taskConfigurationBean.create();
	final Context context = getContext(request);
	request.setAttribute("task", task);
	return context.forward("/myorg/viewTask.jsp");
    }

    public ActionForward viewTask(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Task task = getDomainObject(request, "taskId");
	request.setAttribute("task", task);
	return context.forward("/myorg/viewTask.jsp");
    }

    public ActionForward deleteTaskConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final TaskConfiguration taskConfiguration = getDomainObject(request, "taskConfigurationId");
	int taskConfigurationCount = taskConfiguration.getTask().getTaskConfigurationsCount();
	taskConfiguration.delete();
	return taskConfigurationCount == 1 ? viewScheduler(mapping, form, request, response) : viewTask(mapping, form, request,
		response);
    }

    public ActionForward viewTaskLog(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final TaskLog taskLog = getDomainObject(request, "taskLogId");
	request.setAttribute("taskLog", taskLog);
	return context.forward("/myorg/viewTaskLog.jsp");
    }

    public ActionForward prepareLoadAndRun(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	ClassBean classBean = getRenderedObject();
	if (classBean == null) {
	    classBean = new ClassBean();
	}
	request.setAttribute("classBean", classBean);
	return forward(request, "/myorg/loadAndRun.jsp");
    }

    public ActionForward loadAndRun(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final ClassBean classBean = getRenderedObject();
	classBean.run();
	request.setAttribute("classBean", classBean);
	return forward(request, "/myorg/loadAndRun.jsp");
    }

}
