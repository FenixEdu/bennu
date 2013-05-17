package pt.ist.bennu.scheduler.domain;

import pt.ist.bennu.scheduler.CronTask;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TaskSchedule extends TaskSchedule_Base {

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
        setSchedulerSystem(null);
        super.deleteDomainObject();
    }

    @Atomic(mode = TxMode.READ)
    public TaskRunner getTaskRunner() {
        Class<? extends CronTask> taskClass;
        try {
            taskClass = (Class<? extends CronTask>) Class.forName(getTaskClassName());
            return new TaskRunner(taskClass.newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
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
