package pt.ist.bennu.io.domain;

import java.io.InputStream;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, bb2009
 * 
 */
abstract public class FileStorage extends FileStorage_Base {
    public FileStorage() {
        super();
        setFileSupport(FileSupport.getInstance());
    }

    abstract public String store(String uniqueIdentification, byte[] content);

    abstract public byte[] read(String uniqueIdentification);

    abstract public InputStream readAsInputStream(String uniqueIdentification);

    public static DomainStorage createNewDomainStorage(final String name) {
        return new DomainStorage(name);
    }

    public static LocalFileSystemStorage createNewFileSystemStorage(String name, String path, Integer treeDirectoriesNameLength) {
        return new LocalFileSystemStorage(name, path, treeDirectoriesNameLength);
    }

    public Boolean delete() {
        if (isCanBeDeleted()) {
            getConfigurations().clear();
            removeFileSupport();
            deleteDomainObject();
            return true;
        }
        return false;
    }

    public boolean isCanBeDeleted() {
        return getFilesCount() == 0;
    }
}
