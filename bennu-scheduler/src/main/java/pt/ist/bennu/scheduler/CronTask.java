package pt.ist.bennu.scheduler;

import java.io.File;
import java.io.IOException;

import jvstm.TransactionalCommand;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.domain.SchedulerSystem;
import pt.ist.fenixframework.pstm.Transaction;

public abstract class CronTask implements Runnable {
    private Logger logger;
    private static final String TASK_PATH = "/tmp/";

    public String getLocalizedName() {
        return SchedulerSystem.getTaskName(getClassName());
    }

    private String getClassName() {
        return this.getClass().getName();
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(getClass());
        }
        return logger;
    }

    public abstract void runTask();

    @Override
    public void run() {
        getLogger().info("Start at {}", new DateTime());
        Transaction.withTransaction(false, new TransactionalCommand() {

            @Override
            public void doIt() {
                runTask();
            }
        });
        getLogger().info("End at {}", new DateTime());
    }

    private String getPath() {
        return TASK_PATH.concat(getClassName()).concat("/");
    }

    public File output(String filename, byte[] fileContent) {
        try {
            File outputFile = new File(getPath() + filename);
            FileUtils.writeByteArrayToFile(outputFile, fileContent);
            return outputFile;
        } catch (IOException e) {
            return null;
        }
    }

}
