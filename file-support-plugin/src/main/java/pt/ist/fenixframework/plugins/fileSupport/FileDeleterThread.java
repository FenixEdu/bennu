package pt.ist.fenixframework.plugins.fileSupport;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;
import pt.ist.fenixframework.plugins.fileSupport.domain.LocalFileToDelete;

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
        for (final LocalFileToDelete localFileToDelete : new ArrayList<LocalFileToDelete>(FileSupport.getInstance()
                .getLocalFilesToDelete())) {
            logger.info("Deleting: " + localFileToDelete.getFilePath());
            try {
                localFileToDelete.delete();
            } catch (Exception e) {
                logger.debug("Failed to delete file", e);
            }
        }
    }
}
