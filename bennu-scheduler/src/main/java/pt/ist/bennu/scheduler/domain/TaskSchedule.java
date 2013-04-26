package pt.ist.bennu.scheduler.domain;

import pt.ist.bennu.scheduler.CronTask;

public class TaskSchedule extends TaskSchedule_Base {

    private TaskRunner runner;

    private TaskSchedule(final String taskClassName) {
        super();
        setTaskClassName(taskClassName);
        setSchedulerSystem(SchedulerSystem.getInstance());
    }

    public TaskSchedule(final String taskClassName, final String schedule) {
        this(taskClassName);
        setSchedule(schedule);
        SchedulerSystem.schedule(this);
    }

    public void delete() {
        SchedulerSystem.unschedule(this);
        removeSchedulerSystem();
        super.deleteDomainObject();
    }

    public TaskRunner getTaskRunner() {
        try {
            if (runner == null) {
                Class<? extends CronTask> taskClass = (Class<? extends CronTask>) Class.forName(getTaskClassName());
                runner = new TaskRunner(taskClass.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return runner;
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
}
