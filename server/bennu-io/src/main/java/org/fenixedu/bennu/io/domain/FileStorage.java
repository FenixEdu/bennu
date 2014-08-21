package org.fenixedu.bennu.io.domain;

import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
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

    public boolean isDefault() {
        return getFileSupportAsDefault() != null;
    }

    /**
     * Returns the 'sendfile' path for the given file. <br />
     * 
     * PRIVATE API:
     * Note that this is Bennu IO private API, and should not be used outside this module.
     * If you do use it, do it at your own risk, as this API is subject to change without
     * any warning.
     * 
     * @param file
     *            The file to check
     * @return
     *         An optional sendfile path
     */
    public static Optional<String> sendfilePath(GenericFile file) {
        return file.getStorage().getSendfilePath(file.getContentKey());
    }

    /*
     * Retrieves the file path for the given content key,
     * if this storage supports it, and the file is indeed stored
     * in the file system.
     * 
     * By default returns an empty optional.
     */
    Optional<String> getSendfilePath(String contentKey) {
        return Optional.empty();
    }
}
