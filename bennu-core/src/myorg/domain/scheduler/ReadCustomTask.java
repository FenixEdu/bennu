package myorg.domain.scheduler;

public abstract class ReadCustomTask extends TransactionalCustomTask {

    protected boolean readOnly() {
	return true;
    }

}
