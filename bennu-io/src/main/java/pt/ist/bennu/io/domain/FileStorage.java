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

    // @Service
    public static DomainStorage createNewDomainStorage(final String name) {
        return new DomainStorage(name);
    }

    // @Service
    public static LocalFileSystemStorage createNewFileSystemStorage(String name, String path, Integer treeDirectoriesNameLength) {
        return new LocalFileSystemStorage(name, path, treeDirectoriesNameLength);
    }

    // @Service
    public void delete() {
        if (isCanBeDeleted()) {
            getConfigurations().clear();
            removeFileSupport();
            deleteDomainObject();
        }
    }

    public boolean isCanBeDeleted() {
        return getFilesCount() == 0;
    }

    // abstract public Collection<Pair<String, String>>
    // getPresentationDetails();
}
