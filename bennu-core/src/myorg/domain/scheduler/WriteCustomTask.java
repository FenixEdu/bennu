package myorg.domain.scheduler;

public abstract class WriteCustomTask extends TransactionalCustomTask {

    protected boolean readOnly() {
	return false;
    }

}
