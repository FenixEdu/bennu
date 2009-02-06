/*
 * @(#)Task.java
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

package myorg.domain.scheduler;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.TreeSet;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.services.Service;
import dml.DomainClass;
import dml.DomainModel;

public abstract class Task extends Task_Base {

    public static final Comparator<Task> COMPARATOR_BY_LOCALIZED_NAME = new Comparator<Task>() {

	@Override
	public int compare(final Task task1, final Task task2) {
	    final int c = task1.getLocalizedName().compareTo(task2.getLocalizedName());
	    return c == 0 ? task1.getIdInternal().compareTo(task2.getIdInternal()) : c;
	}
	
    };

    private final TimerTask timerTask = new TimerTask() {

	// This reference will garantee that the task instance will not be GC'ed while the
	// TimerTask is still scheduled in the Timer.
	private final Task task = getThis();

	@Override
	public void run() {
	    final TaskExecutor taskExecutor = new TaskExecutor(task);
	    taskExecutor.start();
	    try {
		taskExecutor.join();
	    } catch (final InterruptedException e) {
		e.printStackTrace();
	    }
	}

    };

    public Task() {
        super();
        setOjbConcreteClass(getClass().getName());
        setMyOrg(MyOrg.getInstance());
    }

    private Task getThis() {
	return this;
    }

    @Service
    public static void initTasks() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	final DomainModel domainModel = FenixWebFramework.getDomainModel();
	for (final DomainClass domainClass : domainModel.getDomainClasses()) {
	    if (isTaskInstance(domainClass) && !existsTaskInstance(domainClass)) {
		initTask(domainClass);
	    }
	}
    }

    public static SortedSet<Task> getTasksSortedByLocalizedName() {
	final SortedSet<Task> tasks = new TreeSet<Task>(COMPARATOR_BY_LOCALIZED_NAME);
	tasks.addAll(MyOrg.getInstance().getTasksSet());
	return tasks;
    }

    public static SortedSet<Task> getTasksSortedByLocalizedName(boolean active) {
	final SortedSet<Task> tasks = new TreeSet<Task>(COMPARATOR_BY_LOCALIZED_NAME);
	for (final Task task : MyOrg.getInstance().getTasksSet()) {
	    if (task.getTaskConfigurationsSet().isEmpty() != active) {
		tasks.add(task);
	    }
	}
	return tasks;
    }

    private static boolean existsTaskInstance(final DomainClass domainClass) {
	final String classname = domainClass.getFullName();
	for (final Task task : MyOrg.getInstance().getTasksSet()) {
	    if (task.getOjbConcreteClass().equals(classname)) {
		return true;
	    }
	}
	return false;
    }

    private static void initTask(final DomainClass domainClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	final Class taskClass = Class.forName(domainClass.getFullName());
	taskClass.newInstance();
    }

    private static boolean isTask(final DomainClass domainClass) {
	return domainClass != null && domainClass.getFullName().equals(Task.class.getName());
    }

    private static boolean isTaskInstance(final DomainClass domainClass) {
	if (domainClass == null || isTask(domainClass)) {
	    return false;
	}
	final DomainClass superclass = (DomainClass) domainClass.getSuperclass();
	return isTask(superclass) || isTaskInstance(superclass);
    }

    public abstract String getLocalizedName();

    public abstract void executeTask();

    @Service
    public void createTaskConfiguration(final TaskConfigurationBean taskConfigurationBean) {
	new TaskConfiguration(taskConfigurationBean);
    }

    public void createNewLog() {
	final TaskLog taskLog = new TaskLog(this);
	setLastRun(taskLog.getTaskStart());
    }

    public void updateLastLog(final Boolean successful) {
	final TaskLog taskLog = getLastLog();
	taskLog.update(successful);
    }

    private TaskLog getLastLog() {
	return Collections.max(getTaskLogsSet(), TaskLog.COMPARATOR_BY_START);
    }

}
