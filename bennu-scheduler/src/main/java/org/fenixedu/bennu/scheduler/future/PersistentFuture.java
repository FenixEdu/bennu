package org.fenixedu.bennu.scheduler.future;

import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class PersistentFuture extends PersistentFuture_Base {

    public PersistentFuture(SerializableRunnable task, String shortDescription) {
        this.setTask(task);
        this.setShortDescription(shortDescription);

        this.setState(PersistentFutureState.WAITING);
        this.setCreated(DateTime.now());

        final FutureSystem fs = FutureSystem.getInstance();
        this.setFutureSystem(fs);
        this.setIncompleteFutureSystem(fs);
    }

    private SerializableRunnable getTask() {
        final PersistentFutureTask persistentFutureTask = super.getPersistentFutureTask();
        if (persistentFutureTask == null) {
            return null;
        }
        return (SerializableRunnable) persistentFutureTask.getTask();
    }

    private void setTask(SerializableRunnable task) {
        super.setPersistentFutureTask(new PersistentFutureTask(() -> {
            task.run();
        }, this));
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void execute() {
        if (isDone()) {
            return;
        }

        this.setState(PersistentFutureState.PROCESSING);
        this.setStartedExecution(DateTime.now());

        try {
            final Runnable task = this.getTask();
            task.run();
            this.finish(true);
        } catch (final Exception e) {
            this.finish(false);
            throw e;
        }
    }

    public boolean isDone() {
        return this.getState().isDone();
    }

    private void finish(boolean success) {
        this.setState(success ? PersistentFutureState.SUCCESS : PersistentFutureState.FAILURE);
        this.setIncompleteFutureSystem(null);
        this.setFinishedExecution(DateTime.now());
    }

    public boolean isCancelled() {
        return this.getState().equals(PersistentFutureState.CANCELLED);
    }

    public boolean cancel() {
        if (isDone()) {
            return false;
        }
        finish(false);
        this.setState(PersistentFutureState.CANCELLED);
        return true;
    }

    public boolean isSuccess() {
        return this.getState().equals(PersistentFutureState.SUCCESS);
    }

    public boolean isFailure() {
        return this.getState().equals(PersistentFutureState.FAILURE);
    }

    public void delete() {
        super.getPersistentFutureTask().delete();
        super.setFutureSystem(null);
        super.setIncompleteFutureSystem(null);
        super.deleteDomainObject();
    }
}
