package org.fenixedu.bennu.io.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     * Attempts to download the given file into the given response, in a more efficient manner than loading the file into memory
     * and dumping it into the output stream.
     * 
     * This method returns a boolean indicating whether the low-level download was performed. If not, callers are expected to
     * handle the file download in the regular manner.
     * 
     * @param file
     *            The file to download
     * @param request
     *            The HttpServletRequest that originated the file download request
     * @param response
     *            The HttpServletResponse to which the response should be written to
     * @param start
     *            The start offset within the file. The value of this number MUST be between 0 and the size of the file
     * @param end
     *            The end offset within the file. The value within this number MUST be between 1 and the size of the file
     * @return
     *         Whether a low-level download was performed
     * @throws IOException
     *             If an exception occurs while downloading the file
     * @throws NullPointerException
     *             If any of the arguments is null
     */
    public static boolean tryDownloadFile(GenericFile file, HttpServletRequest request, HttpServletResponse response, long start,
            long end) throws IOException {
        return file.getFileStorage().tryLowLevelDownload(file, request, response, start, end);
    }

    /**
     * Attempts to perform a low-level download of the given file into the given response. {@link FileStorage} instances are
     * expected to implement this whenever possible.
     * 
     * The default implementation returns {@code false}, as just tells any caller to fallback to downloading the file in the
     * regular manner.
     * 
     * @param file
     *            The file to download
     * @param request
     *            The HttpServletRequest that originated the file download request
     * @param response
     *            The HttpServletResponse to which the response should be written to
     * @param start
     *            The start offset within the file. The value of this number MUST be between 0 and the size of the file
     * @param end
     *            The end offset within the file. The value within this number MUST be between 1 and the size of the file
     * @return
     *         Whether a low-level download was performed
     * @throws IOException
     *             If an exception occurs while downloading the file
     */
    protected boolean tryLowLevelDownload(GenericFile file, HttpServletRequest request, HttpServletResponse response, long start,
            long end) throws IOException {
        return false;
    }
}
