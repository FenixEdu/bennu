package pt.ist.bennu.io;

import java.util.ArrayList;

import jvstm.TransactionalCommand;

import org.apache.log4j.Logger;

import pt.ist.bennu.io.domain.FileSupport;
import pt.ist.bennu.io.domain.LocalFileToDelete;
import pt.ist.fenixframework.pstm.Transaction;

public class FileDeleterThread implements Runnable {

    private static final long SLEEP_TIME = 300000;
    private static final Logger logger = Logger.getLogger(FileDeleterThread.class.getName());

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

    private void process() {
        Transaction.withTransaction(new TransactionalCommand() {

            @Override
            public void doIt() {
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

        });
    }
}
