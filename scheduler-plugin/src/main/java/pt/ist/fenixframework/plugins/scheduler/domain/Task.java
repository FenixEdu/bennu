package pt.ist.fenixframework.plugins.scheduler.domain;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;
import dml-maven-plugin.pt.ist.fenixframework.plugins.scheduler.domain.Task_Base;

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
