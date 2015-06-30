package org.fenixedu.bennu.io.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tika.Tika;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.commons.StringNormalizer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Strings;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 */
public abstract class GenericFile extends GenericFile_Base {
    private static final Logger logger = LoggerFactory.getLogger(GenericFile.class);

    /**
     * Used to detect file content type. {@link Tika#detect(byte[], String)} is thread-safe.
     */
    private static final Tika tika = new Tika();

    protected GenericFile() {
        super();
        setFileSupport(FileSupport.getInstance());
        setCreationDate(new DateTime());
    }

    protected void init(String displayName, String filename, byte[] content) {
        if (content == null) {
            throw new NullPointerException("Content byte[] is null");
        }
        setDisplayName(displayName);
        setFilename(filename);
        setContent(content);
    }

    /**
     * Initializes this file with the contents of the provided {@link File}.
     * 
     * @param displayName
     *            The pretty name for this file
     * @param filename
     *            The low-level filename for this file
     * @param file
     *            The file from which the contents of the newly created file are based upon
     * @throws IOException
     *             If an error occurs while reading the input file or storing it in the underlying storage
     */
    protected void init(String displayName, String filename, File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("Content is null");
        }
        setDisplayName(displayName);
        setFilename(filename);
        setContent(file, filename);
    }

    public abstract boolean isAccessible(User user);

    public boolean isPrivate() {
        return !isAccessible(null);
    }

    @Override
    public DateTime getCreationDate() {
        //FIXME: remove when the framework enables read-only slots
        return super.getCreationDate();
    }

    @Override
    public Long getSize() {
        //FIXME: remove when the framework enables read-only slots
        return super.getSize();
    }

    @Override
    public String getContentType() {
        //FIXME: remove when the framework enables read-only slots
        return super.getContentType();
    }

    /**
     * Returns the checksum for this file, using the algorithm specified by {@link #getChecksumAlgorithm()}.
     * 
     * Due to performance concerns, this value is computed lazily, and may be cached in the domain. To ensure the value is cached,
     * you should invoke the {@link #ensureChecksum()} method before any operation that manipulates a large number of files.
     * 
     * @return
     *         The checksum of this file, never {@code null}
     */
    @Override
    public String getChecksum() {
        if (super.getChecksum() == null) {
            return computeChecksum();
        } else {
            return super.getChecksum();
        }
    }

    /**
     * Returns the algorithm used to compute the checksum of this file.
     * 
     * @return
     *         The algorithm used to compute the checksum of this file, never {@code null}
     */
    @Override
    public String getChecksumAlgorithm() {
        if (super.getChecksumAlgorithm() == null) {
            return DEFAULT_CHECKSUM_ALGORITHM;
        } else {
            return super.getChecksumAlgorithm();
        }
    }

    /**
     * Ensures that this file's checksum is stored in the database, thus increasing the performance of {@link #getChecksum()}.
     */
    public void ensureChecksum() {
        if (super.getChecksum() == null) {
            setChecksum(computeChecksum());
            setChecksumAlgorithm(DEFAULT_CHECKSUM_ALGORITHM);
        }
    }

    // Always ensure these two are synchronized
    private static final String DEFAULT_CHECKSUM_ALGORITHM = "murmur3_128";
    private static final HashFunction DEFAULT_HASH_FUNCTION = Hashing.murmur3_128();

    private String computeChecksum() {
        Hasher hasher = DEFAULT_HASH_FUNCTION.newHasher();
        try (InputStream stream = getStream(); OutputStream out = Funnels.asOutputStream(hasher)) {
            ByteStreams.copy(stream, out);
            return hasher.hash().toString();
        } catch (IOException e) {
            throw new RuntimeException("Cannot compute checksum for " + getExternalId(), e);
        }
    }

    @Override
    public void setFilename(String filename) {
        final String nicerFilename = filename.substring(filename.lastIndexOf('/') + 1);
        final String normalizedFilename = StringNormalizer.normalizePreservingCapitalizedLetters(nicerFilename);
        super.setFilename(normalizedFilename);
        if (getContentKey() != null) {
            //no point in calculating the content type before content is set
            setContentType(detectContentType(getContent(), normalizedFilename));
        }
    }

    private void setContent(File file, String filename) throws IOException {
        long size = file.length();
        setSize(Long.valueOf(size));
        final FileStorage fileStorage = getFileStorage();
        final String uniqueIdentification =
                fileStorage.store(Strings.isNullOrEmpty(getContentKey()) ? getExternalId() : getContentKey(), file);
        setStorage(fileStorage);

        if (Strings.isNullOrEmpty(uniqueIdentification)) {
            throw new RuntimeException();
        }

        setContentKey(uniqueIdentification);
        setContentType(detectContentType(file, filename));
    }

    private void setContent(byte[] content) {
        long size = (content == null) ? 0 : content.length;
        setSize(Long.valueOf(size));
        final FileStorage fileStorage = getFileStorage();
        final String uniqueIdentification =
                fileStorage.store(Strings.isNullOrEmpty(getContentKey()) ? getExternalId() : getContentKey(), content);
        setStorage(fileStorage);

        if (Strings.isNullOrEmpty(uniqueIdentification) && content != null) {
            throw new RuntimeException();
        }

        setContentKey(uniqueIdentification);
        if (content != null) {
            setContentType(detectContentType(content, getFilename()));
        }
    }

    public byte[] getContent() {
        return getStorage().read(getContentKey());
    }

    public InputStream getStream() {
        return getStorage().readAsInputStream(getContentKey());
    }

    public static void convertFileStorages(final FileStorage fileStorageToUpdate) {
        if (fileStorageToUpdate != null) {
            try {
                for (final GenericFile genericFile : FileSupport.getInstance().getFileSet()) {
                    if (fileStorageToUpdate == genericFile.getFileStorage() && fileStorageToUpdate != genericFile.getStorage()) {
                        genericFile.updateFileStorage();
                    }
                }
                logger.debug("FILE Conversion: DONE SUCESSFULLY!");
            } catch (Throwable e) {
                logger.debug("FILE Conversion: ABORTED!!!");
                e.printStackTrace();
            }
        }
    }

    @Atomic
    private void updateFileStorage() {
        setContent(getContent());
    }

    protected FileStorage getFileStorage() {
        final FileStorage fileStorage = FileStorageConfiguration.readFileStorageByFileType(getClass().getName());
        if (fileStorage == null) {
            return FileSupport.getInstance().getDefaultStorage();
        }
        return fileStorage;
    }

    /**
     * Guessing file content type with {@link javax.activation.MimetypesFileTypeMap} is not enough.
     *
     * @param filename
     *            The name of the file to evaluate
     * @return file content type
     *         The detected file type, from the given file name
     * @deprecated content detection is done automatically, no need for this method
     */
    @Deprecated
    protected String guessContentType(final String filename) {
        return new MimetypesFileTypeMap().getContentType(filename);
    }

    /**
     * Detect content type based on file content "magic" bytes. Fallback to filename extension if file content is inconclusive.
     * 
     * @param content
     *            The content of the file, for magic byte analysis
     * @param filename
     *            The name of the file to evaluate
     *
     * @return the detected mime-type. application/octet-stream returned when detection was not successful.
     *
     * @see Tika
     */
    protected String detectContentType(byte[] content, String filename) {
        return tika.detect(content, filename);
    }

    /**
     * Detect content type based on file content "magic" bytes. Fallback to filename extension if file content is inconclusive.
     *
     * @return the detected mime-type. application/octet-stream returned when detection was not successful.
     *
     * @see Tika
     */
    private static final String detectContentType(File file, String filename) throws IOException {
        try (InputStream stream = new FileInputStream(file)) {
            return tika.detect(stream, filename);
        }
    }

    public void delete() {
        setContent(null);
        setStorage(null);
        setFileSupport(null);
        deleteDomainObject();
    }

    @SuppressWarnings("unchecked")
    public static <T extends GenericFile> List<T> getFiles(final Class<T> clazz) {
        final List<T> files = new ArrayList<>();
        for (final GenericFile file : FileSupport.getInstance().getFileSet()) {
            if (file.getClass().equals(clazz)) {
                files.add((T) file);
            }
        }
        return files;
    }
}
