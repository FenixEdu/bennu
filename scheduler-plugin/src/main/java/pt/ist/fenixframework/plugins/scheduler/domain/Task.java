package pt.ist.fenixframework.plugins.scheduler.domain;

import pt.ist.fenixframework.Atomic;

public abstract class Task extends Task_Base {

    public Task() {
        super();
        setSchedulerSystem(SchedulerSystem.getInstance());
    }

//    private void checkUnique() {
//	for (final Task otherTask : getSchedulerSystem().getTaskSet()) {
//	    if (otherTask != this && otherTask.getClass() == getClass()) {
//		throw new Error("There can only be one task of each type!");
//	    }
//	}
//    }

    public void queue(final Task task) {
        if (getClass() != task.getClass()) {
            if (hasNextTask()) {
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
//	    throw new Error(e);
        }
    }

    public abstract void runTask();

    private class TaskThread extends Thread {

        @Override
        @Atomic(readOnly = true)
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
