package org.fenixedu.bennu.io.domain;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, bb2009
 * 
 */
public abstract class FileStorage extends FileStorage_Base {
    protected FileStorage() {
        super();
        setFileSupport(FileSupport.getInstance());
    }

    @Override
    public String getName() {
        //FIXME: remove when the framework enables read-only slots
        return super.getName();
    }

    @Override
    public Set<GenericFile> getFileSet() {
        //FIXME: remove when the framework enables read-only slots
        return Collections.unmodifiableSet(super.getFileSet());
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
            getConfigurationSet().clear();
            setFileSupport(null);
            deleteDomainObject();
            return true;
        }
        return false;
    }

    public boolean isCanBeDeleted() {
        return getFileSet().isEmpty();
    }
}
