package org.fenixedu.bennu.io.domain;

import java.io.File;

/**
 * 
 * @author Shezad Anavarali Date: Aug 12, 2009
 * 
 */
final class LocalFileToDelete extends LocalFileToDelete_Base {
    public LocalFileToDelete() {
        super();
        setFileSupport(FileSupport.getInstance());
    }

    public LocalFileToDelete(String path) {
        this();
        setFilePath(path);
    }

    @Override
    public String getFilePath() {
        //FIXME: remove when the framework enables read-only slots
        return super.getFilePath();
    }

    public void delete() {
        final File existingFile = new File(getFilePath());
        if (!existingFile.exists() || existingFile.delete()) {
            setFileSupport(null);
            deleteDomainObject();
        }
    }
}