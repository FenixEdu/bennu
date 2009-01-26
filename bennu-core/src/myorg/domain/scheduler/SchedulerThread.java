package myorg.domain.scheduler;

import jvstm.TransactionalCommand;
import myorg.domain.MyOrg;

import org.joda.time.DateTime;

import pt.ist.fenixframework.pstm.Transaction;

public class SchedulerThread extends Thread implements TransactionalCommand {

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(true, this);
    }

    @Override
    public void doIt() {
	for (final Task task : MyOrg.getInstance().getTasksSet()) {
	    for (final TaskConfiguration taskConfiguration : task.getTaskConfigurationsSet()) {
		if (taskConfiguration.shouldRunNow()) {
		    logTaskStart(task);
		    boolean successful = false;
		    try {
			final TaskExecutor taskExecutor = new TaskExecutor(task);
			taskExecutor.start();
			try {
			    taskExecutor.join();
			    successful = true;;
			} catch (InterruptedException e) {
			    throw new Error(e);
			}
		    } finally {
			logTaskEnd(task, successful);
		    }
		}
	    }
	}
    }

    private void logTaskStart(final Task task) {
	final TaskLogger taskLogger = new TaskLogger(task);
	logTask(taskLogger);
    }

    private void logTaskEnd(final Task task, final boolean successful) {
	final TaskLogger taskLogger = new TaskLogger(task, successful);
	logTask(taskLogger);
    }

    private void logTask(final TaskLogger taskLogger) throws Error {
	taskLogger.start();
	try {
	    taskLogger.join();
	} catch (InterruptedException e) {
	    throw new Error(e);
	}
    }

}
