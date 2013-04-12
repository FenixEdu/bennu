package pt.ist.bennu.io.domain;


/**
 * 
 * @author Shezad Anavarali Date: Aug 11, 2009
 * 
 */
public class FileRawData extends FileRawData_Base {

    public FileRawData() {
        super();
        setFileSupport(FileSupport.getInstance());
    }

    public FileRawData(String uniqueIdentification, byte[] content) {
        this();
        setContent(content);
        setContentKey(uniqueIdentification);
    }

    public void delete() {
        removeFileSupport();
        deleteDomainObject();
    }
}
