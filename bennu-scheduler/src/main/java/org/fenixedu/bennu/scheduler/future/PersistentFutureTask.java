package org.fenixedu.bennu.scheduler.future;

public class PersistentFutureTask extends PersistentFutureTask_Base {

    public PersistentFutureTask(SerializableRunnable task, PersistentFuture persistentFuture) {
        super();

        super.setTask(task);
        super.setPersistentFuture(persistentFuture);
    }

    public void delete() {
        super.setPersistentFuture(null);
        super.deleteDomainObject();
    }

}
