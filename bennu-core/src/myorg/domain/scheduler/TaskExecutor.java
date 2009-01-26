package myorg.domain.scheduler;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;

public class TaskExecutor extends Thread {

    private long taskId;

    public TaskExecutor(final Task task) {
	taskId = task.getOID();
    }

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(false, new TransactionalCommand() {

	    @Override
	    public void doIt() {
		final Task task = (Task) Transaction.getObjectForOID(taskId);
		task.executeTask();
	    }
	    
	});
    }

}
