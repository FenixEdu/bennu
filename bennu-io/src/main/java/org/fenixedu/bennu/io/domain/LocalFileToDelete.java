package org.fenixedu.bennu.io.domain;

import java.io.File;

/**
 * 
 * @author Shezad Anavarali Date: Aug 12, 2009
 * 
 */
public final class LocalFileToDelete extends LocalFileToDelete_Base {
    public LocalFileToDelete() {
        setFileSupport(FileSupport.getInstance());
    }

    public LocalFileToDelete(final String path) {
        this();
        setFilePath(path);
    }

    @Override
    public String getFilePath() {
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