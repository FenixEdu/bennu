package pt.ist.fenixframework.plugins.scheduler.domain;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public abstract class Task extends Task_Base {

    public Task() {
        super();
        setSchedulerSystem(SchedulerSystem.getInstance());
    }

    public void queue(final Task task) {
        if (getClass() != task.getClass()) {
            if (getNextTask() != null) {
                getNextTask().queue(task);
            } else {
                setNextTask(task);
            }
        }
    }

    public boolean shouldRunNow() {
        for (final TaskSchedule taskSchedule : getTaskScheduleSet()) {
            if (taskSchedule.shouldRunNow()) {
                return true;
            }
        }
        return false;
    }

    public void runPendingTask() {
        final TaskThread taskThread = new TaskThread();
        taskThread.start();
        try {
            taskThread.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void runTask();

    private class TaskThread extends Thread {

        @Override
        @Atomic(mode = TxMode.READ)
        public void run() {
            runTask();
        }
    }

    public void clearAllSchedules() {
        for (final TaskSchedule schedule : getTaskScheduleSet()) {
            schedule.delete();
        }
    }

}
