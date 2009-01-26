package myorg.domain.scheduler;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;

public class TaskLogger extends Thread {

    private final long taskId;
    private final Boolean successful;

    public TaskLogger(final Task task) {
	taskId = task.getOID();
	this.successful = null;
    }

    public TaskLogger(final Task task, final boolean successful) {
	taskId = task.getOID();
	this.successful = Boolean.valueOf(successful);
    }

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(false, new TransactionalCommand() {

	    @Override
	    public void doIt() {
		final Task task = (Task) Transaction.getObjectForOID(taskId);
		if (successful == null) {
		    task.createNewLog();
		} else {
		    task.updateLastLog(successful);
		}
	    }
	    
	});
    }

}
