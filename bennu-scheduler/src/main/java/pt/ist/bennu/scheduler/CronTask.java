package pt.ist.bennu.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.domain.SchedulerSystem;
import pt.ist.bennu.scheduler.log.ExecutionLog;
import pt.ist.fenixframework.Atomic;

import com.google.common.base.Joiner;

public abstract class CronTask implements Runnable {
    private Logger logger;
    private transient ExecutionLog log;

    public String getLocalizedName() {
        return SchedulerSystem.getTaskName(getClassName());
    }

    private String getClassName() {
        return this.getClass().getName();
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(getClassName());
            log.addFile("log");
        }
        return logger;
    }

    public abstract void runTask();

    @Override
    public void run() {
        log = new ExecutionLog(getClassName());
        log.persist();
        try {
            innerAtomicRun();
            log.setSuccess(true);
        } catch (Throwable t) {
            t.printStackTrace();
            log.setSuccess(false);
            log.setError(t);
        } finally {
            log.setEnd(new DateTime());
            log.persist();
            logger = null;
        }
    }

    @Atomic
    private void innerAtomicRun() {
        runTask();
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

    public File output(String filename, byte[] fileContent, boolean append) {
        final String absPath = getAbsolutePath(filename);
        final File file = new File(absPath);
        file.getParentFile().mkdirs();
        getLogger().debug("Write to {}", absPath);
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
        output("log", String.format(format, args).getBytes(), true);
    }

    protected final void taskLog(String log) {
        output("log", log.getBytes(), true);
    }
}
