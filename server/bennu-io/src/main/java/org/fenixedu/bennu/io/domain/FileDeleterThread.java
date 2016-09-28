package org.fenixedu.bennu.io.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

public class FileDeleterThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FileDeleterThread.class.getName());

    @Override
    public void run() {
        FenixFramework.atomic(() -> {
            FileSupport.getInstance().getDeleteSet().stream().forEach(FileDeleterThread::delete);
        });
    }

    private static void delete(LocalFileToDelete file) {
        logger.debug("Deleting: {}", file.getFilePath());
        try {
            file.delete();
        } catch (Exception e) {
            logger.debug("Failed to delete file", e);
        }
    }
}