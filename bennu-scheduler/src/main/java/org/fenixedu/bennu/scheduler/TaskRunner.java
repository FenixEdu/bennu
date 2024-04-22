package org.fenixedu.bennu.scheduler;

import com.google.common.base.Objects;

public class TaskRunner implements Runnable {

    final CronTask task;
    String taskId;

    @SuppressWarnings("unchecked")
    public TaskRunner(final String cronTaskClassName) throws Exception {
        try {
            final Class<? extends CronTask> taskClass = (Class<? extends CronTask>) Class.forName(cronTaskClassName);
            this.task = taskClass.newInstance();
            setTaskId(null);
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw e;
        }
    }

    public TaskRunner(final CronTask task) {
        this.task = task;
        setTaskId(null);
    }

    @Override
    public void run() {
        task.run();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TaskRunner taskRunner && taskRunner.getTaskName().equals(getTaskName());
    }

    public String getTaskName() {
        return task.getClass().getName();
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public CronTask getTask() {
        return task;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTaskName());
    }
}
