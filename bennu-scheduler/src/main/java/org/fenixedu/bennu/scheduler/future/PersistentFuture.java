package org.fenixedu.bennu.scheduler.future;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PersistentFuture extends PersistentFuture_Base implements Future<Serializable> {

    private Future<Serializable> future;

    public PersistentFuture(PersistentFutureTask task, Class<? extends Serializable> resultType) {
        super();

        super.setTask(task);
        this.setResultType(resultType);

        this.setFutureSystem(FutureSystem.getInstance());
    }

    @Override
    public Serializable get() throws InterruptedException, ExecutionException {
        if (isDone()) {
            return this.getResult();
        }
        if (future == null) {
            this.restart();
        }
        return this.future.get();
    }

    @Override
    public Serializable get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (isDone()) {
            return this.getResult();
        }
        if (future == null) {
            this.restart();
        }
        return this.future.get(timeout, unit);
    }

    @Override
    public boolean isDone() {
        return this.getDone();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isDone()) {
            return false;
        }
        if (future == null) {
            this.setCancelled(true);
            this.setDone(true);
            return true;
        }
        final boolean wasCancelled = this.future.cancel(mayInterruptIfRunning);
        this.setCancelled(wasCancelled);
        this.setDone(wasCancelled);
        return wasCancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.getCancelled();
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Serializable> getResultType() throws ClassNotFoundException {
        return (Class<? extends Serializable>) Class.forName(this.getResultTypeName());
    }

    protected void setResultType(Class<? extends Serializable> returnType) {
        this.setResultTypeName(returnType.getName());
    }

    protected void setFuture(Future<Serializable> future) {
        this.future = future;
    }

    protected void restart() {
        // PersistentExecutorService.restart(this);
    }

    @Override
    public void setResult(Serializable result) {
        System.out.println("Setting result to " + result);
        super.setResult(result);
        System.out.println("Result set");
    }

    @Override
    public void setDone(boolean done) {
        System.out.println("Setting done to " + done);
        super.setDone(done);
        System.out.println("Done set");
    }

    public void delete() {
        super.setFutureSystem(null);
        super.deleteDomainObject();
    }
}
