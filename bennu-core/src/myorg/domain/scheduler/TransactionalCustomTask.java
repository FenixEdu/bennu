package myorg.domain.scheduler;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;

public abstract class TransactionalCustomTask extends CustomTask implements TransactionalCommand {

    protected abstract boolean readOnly();

    @Override
    public void run() {
	Transaction.withTransaction(readOnly(), this);
    }

}
