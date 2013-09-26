package pt.ist.bennu.scheduler;

import com.google.common.base.Objects;

public class TaskRunner implements Runnable {

    CronTask task;
    String taskId;

    public TaskRunner(String cronTaskClassName) throws Exception {
        Class<? extends CronTask> taskClass;
        try {
            taskClass = (Class<? extends CronTask>) Class.forName(cronTaskClassName);
            this.task = taskClass.newInstance();
            setTaskId(null);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw e;
        }
    }

    public TaskRunner(CronTask task) {
        this.task = task;
        setTaskId(null);
    }

    @Override
    public void run() {
        task.run();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskRunner) {
            return ((TaskRunner) obj).getTaskName().equals(getTaskName());
        } else {
            return false;
        }
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
