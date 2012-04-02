package pt.ist.fenixframework.plugins.scheduler.domain;

import jvstm.TransactionalCommand;

import org.joda.time.DateTime;

import pt.ist.fenixframework.pstm.Transaction;

public abstract class Task extends Task_Base {

    public Task() {
        super();
        setOjbConcreteClass(getClass().getName());
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
		System.out.println("Scheduler: queueing task: " + task.getClass().getName());
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
	System.out.println("Scheduler: running task: " + getClass().getName());
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
	public void run() {
	    try {
		Transaction.withTransaction(true, new TransactionalCommand() {
		    @Override
		    public void doIt() {
//			setLastRunStart(new DateTime());
			runTask();
//			setLastRunEnd(new DateTime());
		    }
		});
	    } finally {
		Transaction.forceFinish();
	    }
	}

    }

    public void clearAllSchedules() {
	for (final TaskSchedule schedule : getTaskScheduleSet()) {
	    schedule.delete();
	}
    }

}
