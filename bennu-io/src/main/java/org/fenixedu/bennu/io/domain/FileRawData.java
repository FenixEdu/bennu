package org.fenixedu.bennu.io.domain;

/**
 * 
 * @author Shezad Anavarali Date: Aug 11, 2009
 * 
 */
public final class FileRawData extends FileRawData_Base {
    public FileRawData() {
        setFileSupport(FileSupport.getInstance());
    }

    public FileRawData(final String uniqueIdentification, final byte[] content) {
        this();
        setContent(content);
        setContentKey(uniqueIdentification);
    }

    public void delete() {
        setFileSupport(null);
        deleteDomainObject();
    }
}
