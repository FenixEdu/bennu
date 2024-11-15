package org.fenixedu.bennu.scheduler;

import com.google.common.base.Joiner;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;
import org.fenixedu.commons.StringNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.esw.advice.pt.ist.fenixframework.AtomicInstance;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import java.util.concurrent.Callable;

public abstract class CronTask implements Runnable {
    private Logger logger;
    protected transient ExecutionLog log;
    private final Atomic atomic;

    public CronTask() {
        atomic = new AtomicInstance(getTxMode(), true);
    }

    protected TxMode getTxMode() {
        final Task annotation = this.getClass().getAnnotation(Task.class);
        return annotation == null || annotation.readOnly() ? TxMode.READ : TxMode.WRITE;
    }

    public String getLocalizedName() {
        return SchedulerSystem.getTaskName(getClassName());
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public Logger getLogger() {
        return logger == null ? logger = LoggerFactory.getLogger(getClassName()) : logger;
    }

    public abstract void runTask() throws Exception;

    @Override
    public final void run() {
        try {
            log = createExecutionLog();
            SchedulerSystem.getLogRepository().newExecution(log);
            innerAtomicRun();
            updateLog(log.withSuccess());
        } catch (final Throwable t) {
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

    private String sanitize(final String filename) {
        final int i = filename.lastIndexOf(".");
        return i == -1 ? filename : Joiner.on(".").join(StringNormalizer.slugify(filename.substring(0, i)),
                    StringNormalizer.slugify(filename.substring(i + 1, filename.length())));
    }

    public void output(final String filename, final byte[] fileContent, final boolean append) {
        final String sanitized = sanitize(filename);
        SchedulerSystem.getLogRepository().storeFile(log, sanitized, fileContent, append);
        updateLog(log.withFile(sanitized));
    }

    public void output(final String filename, final byte[] fileContent) {
        output(filename, fileContent, false);
    }

    protected final void taskLog(final String format, final Object... args) {
        final String logFormat = args == null || args.length < 1 ? format + "\n" : String.format(format, args);
        SchedulerSystem.getLogRepository().appendTaskLog(log, logFormat);
    }

    protected final void taskLog() {
        taskLog("");
    }

    private void updateLog(final ExecutionLog log) {
        this.log = log;
        SchedulerSystem.getLogRepository().update(log);
    }

}
