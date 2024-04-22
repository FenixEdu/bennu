package org.fenixedu.bennu.scheduler.domain;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.TaskRunner;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TaskSchedule extends TaskSchedule_Base {

    private transient TaskRunner taskRunner;

    {
        taskRunner = null;
    }

    private TaskSchedule(final String taskClassName) {
        super();
        setTaskClassName(taskClassName);
        setSchedulerSystem(SchedulerSystem.getInstance());
        setRunOnce(Boolean.FALSE);
    }

    public TaskSchedule(final String taskClassName, final String schedule) {
        this(taskClassName);
        setSchedule(schedule);
        SchedulerSystem.schedule(this);
    }

    public TaskSchedule(final String taskClassName, Boolean runOnce) {
        this(taskClassName);
        setRunOnce(runOnce);
        SchedulerSystem.schedule(this);
    }

    public void delete() {
        delete(true);
    }

    @Atomic(mode = TxMode.WRITE)
    protected void delete(boolean unschedule) {
        if (unschedule) {
            SchedulerSystem.unschedule(this);
        }
        setSchedulerSystem(null);
        super.deleteDomainObject();
    }

    @Atomic(mode = TxMode.READ)
    public TaskRunner getTaskRunner() {
        if (taskRunner == null) {
            try {
                taskRunner = new TaskRunner(getTaskClassName());
            } catch (final Exception e) {
                throw new Error(e);
            }
        }
        return taskRunner;
    }

    public String getTaskId() {
        return getTaskRunner().getTaskId();
    }

    public void setTaskId(String taskId) {
        getTaskRunner().setTaskId(taskId);
    }

    public CronTask getTask() {
        return getTaskRunner().getTask();
    }

    public Boolean isScheduled() {
        return taskRunner != null && getTaskRunner().getTaskId() != null;
    }

    public Boolean isRunOnce() {
        return getRunOnce() != null && getRunOnce();
    }
}
