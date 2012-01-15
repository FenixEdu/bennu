package pt.ist.emailNotifier.domain;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

public class EmailTask extends EmailTask_Base {

    private static class SingleEmailDispatcher extends Thread {
	private final String oid;

	protected SingleEmailDispatcher(final Email email) {
	    this.oid = email.getExternalId();
	}

	@Override
	public void run() {
	    Transaction.withTransaction(false, new TransactionalCommand() {
		public void doIt() {
		    final Email email = AbstractDomainObject.fromExternalId(oid);
		    email.deliver();
		}
	    });
	}
    }

    @Override
    public void runTask() {
	for (final Email email : EmailNotifier.getInstance().getEmailsSet()) {
	    final SingleEmailDispatcher emailDispatcher = new SingleEmailDispatcher(email);
	    emailDispatcher.start();
	    try {
		emailDispatcher.join();
	    } catch (final InterruptedException e) {
//		throw new Error(e);
	    }
	}
    }
    
}
