package org.fenixedu.bennu.scheduler.future;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pt.ist.fenixframework.Atomic;

public class PersistentExecutorService {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Atomic(mode = Atomic.TxMode.WRITE)
    public static <T extends Serializable> PersistentFuture submit(PersistentFutureTask task, Class<T> resultType) {
        System.out.println("New submission");
        final PersistentFuture persistentFuture = new PersistentFuture(task, resultType);
        System.out.println("Created PersistentFuture " + persistentFuture.getExternalId());

        final Future<Serializable> future = EXECUTOR.submit(() -> {
            return runTask(task, persistentFuture);
        });
        System.out.println("Created Future.");

        persistentFuture.setFuture(future);
        return persistentFuture;
    }

    // @SuppressWarnings("unchecked")
    protected static <T extends Serializable> void restart(PersistentFuture persistentFuture) {
        /*
        System.out.println("Restarting PersistentFuture " + persistentFuture.getExternalId());
        final Callable<T> task = (Callable<T>) persistentFuture.getTask();
        
        final Future<Serializable> future = EXECUTOR.submit(() -> {
            return runTask(task, persistentFuture);
        });
        
        persistentFuture.setFuture(future);
        */
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private static <T extends Serializable> T runTask(Callable<T> task, final PersistentFuture persistentFuture)
            throws Exception {
        System.out.println("Calling task.");
        final T result = task.call();

        System.out.println("Task done. Result is " + result + ". Saving results.");
        persistentFuture.setResult(result);
        persistentFuture.setDone(true);

        System.out.println("Complete.");
        return result;
    }

}
