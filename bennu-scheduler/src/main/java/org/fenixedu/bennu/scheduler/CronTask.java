package org.fenixedu.bennu.scheduler;

import java.util.concurrent.Callable;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.esw.advice.pt.ist.fenixframework.AtomicInstance;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public abstract class CronTask implements Runnable {
    private Logger logger;
    protected transient ExecutionLog log;
    private final Atomic atomic;

    public CronTask() {
        atomic = new AtomicInstance(getTxMode(), true);
    }

    protected TxMode getTxMode() {
        Task annotation = this.getClass().getAnnotation(Task.class);
        return annotation == null || annotation.readOnly() ? TxMode.READ : TxMode.WRITE;
    }

    public String getLocalizedName() {
        return SchedulerSystem.getTaskName(getClassName());
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(getClassName());
        }
        return logger;
    }

    public abstract void runTask() throws Exception;

    @Override
    public final void run() {
        log = createExecutionLog();
        SchedulerSystem.getLogRepository().newExecution(log);
        try {
            innerAtomicRun();
            updateLog(log.withSuccess());
        } catch (Throwable t) {
            t.printStackTrace();
            updateLog(log.withError(t));
        } finally {
            resetLoggers();
            Authenticate.unmock();
        }
    }

    protected ExecutionLog createExecutionLog() {
        return ExecutionLog.newExecutionFor(getClassName());
    }

    private void resetLoggers() {
        logger = null;
        log = null;
    }

    private void innerAtomicRun() throws Exception {
        FenixFramework.getTransactionManager().withTransaction(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                runTask();
                return null;
            }

        }, atomic);
    }

    public void output(String filename, byte[] fileContent, boolean append) {
        SchedulerSystem.getLogRepository().storeFile(log, filename, fileContent, append);
        updateLog(log.withFile(filename));
    }

    public void output(String filename, byte[] fileContent) {
        output(filename, fileContent, false);
    }

    protected final void taskLog(String format, Object... args) {
        if (args == null || args.length < 1) {
            SchedulerSystem.getLogRepository().appendTaskLog(log, format + "\n");
        } else {
            SchedulerSystem.getLogRepository().appendTaskLog(log, String.format(format, args));
        }
    }

    protected final void taskLog() {
        taskLog("");
    }

    private void updateLog(ExecutionLog log) {
        this.log = log;
        SchedulerSystem.getLogRepository().update(log);
    }

}
