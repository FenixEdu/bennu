package pt.ist.bennu.scheduler.domain;

import pt.ist.bennu.scheduler.CronTask;

class TaskRunner implements Runnable {

    CronTask task;
    String taskId;

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
}
