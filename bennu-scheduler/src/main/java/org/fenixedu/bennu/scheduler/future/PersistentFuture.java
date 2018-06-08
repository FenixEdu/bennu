package org.fenixedu.bennu.scheduler.future;

import pt.ist.fenixframework.Atomic;

public class PersistentFuture extends PersistentFuture_Base {

    public PersistentFuture(Runnable task) {
        final PersistentFutureTask persistentTask = () -> {
            task.run();
        };
        super.setTask(persistentTask);
        // try to deserialize and convert back to PersistentFutureTask
        final PersistentFutureTask t = (PersistentFutureTask) this.getTask();

        final FutureSystem fs = FutureSystem.getInstance();
        this.setFutureSystem(fs);
        this.setIncompleteFutureSystem(fs);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void execute() {
        try {
            final PersistentFutureTask task = (PersistentFutureTask) this.getTask();
            task.run();
            this.setSuccess(true);
        } catch (final Exception e) {
            this.setSuccess(false);
        } finally {
            done();
        }
    }

    public boolean isDone() {
        return this.getDone();
    }

    private void done() {
        this.setDone(true);
        this.setIncompleteFutureSystem(null);
    }

    public boolean isCancelled() {
        return this.getCancelled();
    }

    public boolean cancel() {
        if (isDone()) {
            return false;
        }
        this.setCancelled(true);
        done();
        return true;
    }

    public void delete() {
        super.setFutureSystem(null);
        super.setIncompleteFutureSystem(null);
        super.deleteDomainObject();
    }
}
