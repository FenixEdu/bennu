package pt.ist.bennu.io;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.io.domain.FileSupport;
import pt.ist.bennu.io.domain.LocalFileToDelete;
import pt.ist.fenixframework.Atomic;

public class FileDeleterThread implements Runnable {

    private static final long SLEEP_TIME = 300000;
    private static final Logger logger = LoggerFactory.getLogger(FileDeleterThread.class.getName());

    @Override
    public void run() {
        try {
            Thread.sleep(SLEEP_TIME);
            logger.debug("Tick!");
            process();
        } catch (InterruptedException e) {
            // The application is shutting down...
            return;
        }
    }

    @Atomic
    private void process() {
        for (final LocalFileToDelete localFileToDelete : new ArrayList<>(FileSupport.getInstance().getLocalFilesToDeleteSet())) {
            logger.info("Deleting: " + localFileToDelete.getFilePath());
            try {
                localFileToDelete.delete();
            } catch (Exception e) {
                logger.debug("Failed to delete file", e);
            }
        }
    }
}