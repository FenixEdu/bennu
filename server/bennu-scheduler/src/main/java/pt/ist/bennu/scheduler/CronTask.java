package pt.ist.bennu.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.annotation.Task;
import pt.ist.bennu.scheduler.domain.SchedulerSystem;
import pt.ist.bennu.scheduler.log.ExecutionLog;
import pt.ist.esw.advice.pt.ist.fenixframework.AtomicInstance;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Joiner;

public abstract class CronTask implements Runnable {
    private Logger logger;
    protected transient ExecutionLog log;
    private PrintWriter taskLogWriter;
    private Atomic atomic;

    public CronTask() {
        Task annotation = this.getClass().getAnnotation(Task.class);
        atomic = new AtomicInstance(annotation.readOnly() ? TxMode.READ : TxMode.WRITE, true);
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

    public PrintWriter getTaskLogWriter() {
        if (taskLogWriter == null) {
            try {
                File logFile = createFile("log");
                taskLogWriter = new PrintWriter(new FileOutputStream(logFile, true), true);
            } catch (FileNotFoundException e) {
                throw new Error(e);
            }
        }
        return taskLogWriter;
    }

    public abstract void runTask();

    @Override
    public final void run() {
        getExecutionLog().start();
        try {
            innerAtomicRun();
            getExecutionLog().setSuccess(true);
        } catch (Throwable t) {
            t.printStackTrace();
            getExecutionLog().setSuccess(false);
            getExecutionLog().setError(t);
        } finally {
            getExecutionLog().end();
            resetLoggers();
        }
    }

    private void resetLoggers() {
        logger = null;
        log = null;
        taskLogWriter = null;
    }

    public ExecutionLog createLog() {
        return new ExecutionLog(getClassName());
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

    /**
     * AbsolutePath is className/executionId/filename
     * 
     * @param filename
     * @return
     */
    private String getAbsolutePath(String filename) {
        return getAbsolutePath(filename, getClassName(), log.getId());
    }

    public static String getAbsolutePath(String filename, String className, String id) {
        return Joiner.on("/").join(SchedulerSystem.getLogsPath(), className.replace('.', '_'), id.replace('-', '_'), filename);
    }

    private File createFile(String filename) {
        final String absPath = getAbsolutePath(filename);
        final File file = new File(absPath);
        try {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                getLogger().debug("create log dir {}", dir.getAbsolutePath());
                dir.mkdirs();
            }

            if (!file.exists()) {
                getLogger().debug("create log file {}", file.getAbsolutePath());
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public File output(String filename, byte[] fileContent, boolean append) {
        File file = createFile(filename);
        try (FileOutputStream fos = new FileOutputStream(file, append)) {
            fos.write(fileContent);
            fos.flush();
            log.addFile(filename);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File output(String filename, byte[] fileContent) {
        return output(filename, fileContent, false);
    }

    protected final void taskLog(String format, Object... args) {
        PrintWriter writer = getTaskLogWriter();
        if (args == null || args.length < 1) {
            writer.println(format);
        } else {
            writer.printf(format, args);
        }
    }

    protected final void taskLog() {
        taskLog(StringUtils.EMPTY);
    }

    public <T extends ExecutionLog> T getExecutionLog() {
        if (log == null) {
            log = createLog();
        }
        return (T) log;
    }
}
