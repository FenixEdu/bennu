/*
 * @(#)Task.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package pt.ist.bennu.core.domain.scheduler;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.TreeSet;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.DomainModel;
import pt.utl.ist.fenix.tools.util.i18n.Language;

public abstract class Task extends Task_Base {

    public static final Comparator<Task> COMPARATOR_BY_LOCALIZED_NAME = new Comparator<Task>() {

        @Override
        public int compare(final Task task1, final Task task2) {
            final String task1Name = getTaskName(task1);
            final String task2Name = getTaskName(task2);
            final int c = task1Name.compareTo(task2Name);
            return c == 0 ? task1.getExternalId().compareTo(task2.getExternalId()) : c;
        }

        private String getTaskName(final Task task) {
            final String taskName = task.getLocalizedName();
            return taskName == null ? task.getExternalId() : taskName;
        }

    };

    private final TimerTask timerTask = new TimerTask() {

        // This reference will garantee that the task instance will not be GC'ed
        // while the
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

    private StringBuilder log = null;

    public Task() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    private Task getThis() {
        return this;
    }

    @Atomic
    public static void initTasks() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final DomainModel domainModel = FenixFramework.getDomainModel();
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
            if (active && !task.getTaskConfigurationsSet().isEmpty()) {
                tasks.add(task);
            }
            if (!active && task.getTaskConfigurationsSet().isEmpty() && !task.isExecutionPending()) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    private static boolean existsTaskInstance(final DomainClass domainClass) {
        final String classname = domainClass.getFullName();
        for (final Task task : MyOrg.getInstance().getTasksSet()) {
            if (task.getClass().getName().equals(classname)) {
                return true;
            }
        }
        return false;
    }

    private static void initTask(final DomainClass domainClass) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        final Class taskClass = Class.forName(domainClass.getFullName());
        if (!Modifier.isAbstract(taskClass.getModifiers())) {
            taskClass.newInstance();
        }
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

    public boolean isRepeatedOnFailure() {
        return false;
    }

    public boolean isExecutionPending() {
        return MyOrg.getInstance().getPendingExecutionTaskQueue().contains(this);
    }

    public abstract String getLocalizedName();

    public abstract void executeTask();

    @Atomic
    public void createTaskConfiguration(final TaskConfigurationBean taskConfigurationBean) {
        new TaskConfiguration(taskConfigurationBean);
    }

    protected synchronized void logInfo(String msg) {
        if (log == null) {
            log = new StringBuilder();
        }
        log.append(msg + "\n");
    }

    public void createNewLog() {
        final TaskLog taskLog = new TaskLog(this);
        setLastRun(taskLog.getTaskStart());
    }

    public void updateLastLog(final Boolean successful) {
        final TaskLog taskLog = getLastLog();
        taskLog.update(successful, log != null ? log.toString() : null);
        log = null;
    }

    private TaskLog getLastLog() {
        return Collections.max(getTaskLogsSet(), TaskLog.COMPARATOR_BY_START);
    }

    public void cleanupLogs(int maxNumberOfLogs) {
        ArrayList<TaskLog> logs = new ArrayList<TaskLog>(getTaskLogs());
        int logsToRemove = logs.size() - maxNumberOfLogs;
        if (logsToRemove > 0) {
            Collections.sort(logs, TaskLog.COMPARATOR_BY_START);
            for (TaskLog log : logs.subList(0, logsToRemove)) {
                removeTaskLogs(log);
            }
        }
    }

    @Atomic
    public void invokeNow() {
        MyOrg.getInstance().getPendingExecutionTaskQueue().offer(this);
    }

    @Atomic
    public void stop() {
        setPendingExecutionTaskQueue(null);
    }

    public void runPendingTask() {
        System.out.println("Running task: " + getClass().getName());
        log = new StringBuilder();
        final TaskThread taskThread = new TaskThread();
        final DateTime start = new DateTime();
        taskThread.start();
        try {
            taskThread.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
//	    throw new Error(e);
        }
        final DateTime end = new DateTime();
        final LogTaskThread logTaskThread = new LogTaskThread(start, end, taskThread.success, log.toString());
        log = null;
        logTaskThread.start();
        try {
            logTaskThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed run of task: " + getClass().getName());
    }

    private class TaskThread extends Thread {

        private boolean success = false;

        @Override
        public void run() {
            doIt();
            success = true;
        }

        @Atomic
        private void doIt() {
            try {
                // TODO : This needs to be placed in the apps task configuration
                //        and used whenever the app is launchede.
                VirtualHost.setVirtualHostForThread("dot.ist.utl.pt");
                Language.setLocale(Language.getDefaultLocale());
                executeTask();
            } finally {
                VirtualHost.releaseVirtualHostFromThread();
            }
        }

    }

    private class LogTaskThread extends Thread {

        final DateTime start, end;
        final boolean success;
        final String log;

        public LogTaskThread(DateTime start, DateTime end, boolean success, String log) {
            this.start = start;
            this.end = end;
            this.success = success;
            this.log = log;
        }

        @Atomic
        @Override
        public void run() {
            final TaskLog taskLog = new TaskLog(getThis());
            taskLog.setTaskStart(start);
            taskLog.setTaskEnd(end);
            taskLog.setOutput(log);
            taskLog.setSuccessful(Boolean.valueOf(success));
            setLastRun(start);
            cleanupLogs(100);
        }

    }

    @Deprecated
    public java.util.Set<pt.ist.bennu.core.domain.scheduler.TaskLog> getTaskLogs() {
        return getTaskLogsSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.bennu.core.domain.scheduler.TaskConfiguration> getTaskConfigurations() {
        return getTaskConfigurationsSet();
    }

}
