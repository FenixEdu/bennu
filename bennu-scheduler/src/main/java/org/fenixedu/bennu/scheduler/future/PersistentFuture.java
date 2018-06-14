package org.fenixedu.bennu.scheduler.future;

import pt.ist.fenixframework.Atomic;

public class PersistentFuture extends PersistentFuture_Base {

    public PersistentFuture(SerializableRunnable task) {
        this.setTask(task);

        final FutureSystem fs = FutureSystem.getInstance();
        this.setFutureSystem(fs);
        this.setIncompleteFutureSystem(fs);
    }

    public SerializableRunnable getTask() {
        final PersistentFutureTask persistentFutureTask = super.getPersistentFutureTask();
        if (persistentFutureTask == null) {
            return null;
        }
        return (SerializableRunnable) persistentFutureTask.getTask();
    }

    public void setTask(SerializableRunnable task) {
        super.setPersistentFutureTask(new PersistentFutureTask(() -> {
            task.run();
        }, this));
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void execute() {
        if (isDone()) {
            return;
        }

        try {
            final Runnable task = this.getTask();
            task.run();
            this.setSuccess(true);
        } catch (final Exception e) {
            this.setSuccess(false);
            throw e;
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
        super.getPersistentFutureTask().delete();
        super.setFutureSystem(null);
        super.setIncompleteFutureSystem(null);
        super.deleteDomainObject();
    }
}
