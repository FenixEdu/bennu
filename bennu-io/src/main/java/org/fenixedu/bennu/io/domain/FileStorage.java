package org.fenixedu.bennu.io.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.google.common.io.Files;

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

    /**
     * Stores the given file in this storage, and associates it with the given identifier.
     * 
     * This differs from the {@link #store(String, byte[])} variant in that it does not require the whole file to be loaded in
     * memory.
     * 
     * Due to performance reasons, the given file may be locked, moved to another location or even removed.
     * 
     * @param uniqueIdentification
     *            The unique identifier for the newly created file
     * @param file
     *            The file to store
     * @return
     *         The identification associated with the newly created file
     * @throws IOException
     *             If any error occurs while accessing the provided file or storing it in the underlying storage
     */
    public String store(String uniqueIdentification, File file) throws IOException {
        return store(uniqueIdentification, Files.toByteArray(file));
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
     * Returns the 'sendfile' path for the given file.
     * 
     * <strong>PRIVATE API:</strong>
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
