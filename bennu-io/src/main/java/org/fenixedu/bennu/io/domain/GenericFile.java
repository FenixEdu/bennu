package org.fenixedu.bennu.io.domain;

import com.google.common.base.Strings;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;
import org.apache.tika.Tika;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.commons.StringNormalizer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

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
        setFileSupport(FileSupport.getInstance());
        setCreationDate(new DateTime());
    }

    protected void init(final String displayName, final String filename, final byte[] content) {
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
    protected void init(final String displayName, final String filename, final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("Content is null");
        }
        setDisplayName(displayName);
        setFilename(filename);
        setContent(file, filename);
    }
    
    /**
     * Initializes this file with the contents of the provided {@link InputStream}.
     *
     * @param displayName
     *            The pretty name for this file
     * @param filename
     *            The low-level filename for this file
     * @param stream
     *            The stream from which the contents of the newly created file are based upon
     * @throws IOException
     *             If an error occurs while reading the input strean or storing it in the underlying storage
     */
    protected void init(final String displayName, final String filename, final InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("Stream is empty");
        }
        setDisplayName(displayName);
        setFilename(filename);
        setContent(stream, filename);
    }
    
    
    public abstract boolean isAccessible(final User user);

    public boolean isPrivate() {
        return !isAccessible(null);
    }

    @Override
    public DateTime getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    public Long getSize() {
        return super.getSize();
    }

    @Override
    public String getContentType() {
        return super.getContentType();
    }

    @Override
    public String getContentKey() {
        return super.getContentKey();
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
        final String checksum = super.getChecksum();
        return checksum == null ? computeChecksum() : checksum;
    }

    /**
     * Returns the algorithm used to compute the checksum of this file.
     * 
     * @return
     *         The algorithm used to compute the checksum of this file, never {@code null}
     */
    @Override
    public String getChecksumAlgorithm() {
        final String checksumAlgorithm = super.getChecksumAlgorithm();
        return checksumAlgorithm == null ? DEFAULT_CHECKSUM_ALGORITHM : checksumAlgorithm;
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
        final Hasher hasher = DEFAULT_HASH_FUNCTION.newHasher();
        try (final InputStream stream = getStream(); OutputStream out = Funnels.asOutputStream(hasher)) {
            ByteStreams.copy(stream, out);
            return hasher.hash().toString();
        } catch (final IOException e) {
            throw new RuntimeException("Cannot compute checksum for " + getExternalId(), e);
        }
    }

    @Override
    public void setFilename(final String filename) {
        final String nicerFilename = filename.substring(filename.lastIndexOf('/') + 1);
        final String normalizedFilename = StringNormalizer.normalizePreservingCapitalizedLetters(nicerFilename);
        super.setFilename(normalizedFilename);
        if (getContentKey() != null) {
            //no point in calculating the content type before content is set
            setContentType(detectContentType(getContent(), normalizedFilename));
        }
    }

    private void setContent(final File file, final String filename) throws IOException {
        final long size = file.length();
        setSize(Long.valueOf(size));
        final FileStorage fileStorage = getFileStorage();
        final String uniqueIdentification = fileStorage.store(this, file);
        setStorage(fileStorage);

        if (Strings.isNullOrEmpty(uniqueIdentification)) {
            throw new RuntimeException();
        }

        setContentKey(uniqueIdentification);
        setContentType(detectContentType(file, filename));
    }
    
    
    private void setContent(final InputStream stream, final String filename) throws IOException {
        final FileStorage fileStorage = getFileStorage();
        setContentType(tika.detect(stream, filename));
        final CountingInputStream countingStream = new CountingInputStream(stream);
        final String uniqueIdentification = fileStorage.store(this, countingStream);
        setStorage(fileStorage);
        setSize(countingStream.getCount());
        if (Strings.isNullOrEmpty(uniqueIdentification)) {
            throw new RuntimeException();
        }
        setContentKey(uniqueIdentification);
    }
    

    private void setContent(final byte[] content) {
        final long size = (content == null) ? 0 : content.length;
        setSize(Long.valueOf(size));
        final FileStorage fileStorage = getFileStorage();
        final String uniqueIdentification = fileStorage.store(this, content);
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
        return getStorage().read(this);
    }

    public InputStream getStream() {
        return getStorage().readAsInputStream(this);
    }

    public static void convertFileStorages(final FileStorage fileStorageToUpdate) {
        if (fileStorageToUpdate != null) {
            try {
                FileSupport.getInstance().getFileSet().stream()
                        .filter(gf -> fileStorageToUpdate == gf.getFileStorage() && fileStorageToUpdate != gf.getStorage())
                        .forEach(GenericFile::updateFileStorage);
                logger.debug("FILE Conversion: DONE SUCESSFULLY!");
            } catch (final Throwable e) {
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
        return fileStorage == null ? FileSupport.getInstance().getDefaultStorage() : fileStorage;
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
    protected String detectContentType(final byte[] content, final String filename) {
        return tika.detect(content, filename);
    }

    /**
     * Detect content type based on file content "magic" bytes. Fallback to filename extension if file content is inconclusive.
     *
     * @return the detected mime-type. application/octet-stream returned when detection was not successful.
     *
     * @see Tika
     */
    private static final String detectContentType(final File file, final String filename) throws IOException {
        try (final InputStream stream = new FileInputStream(file)) {
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
        return FileSupport.getInstance().getFileSet().stream()
                .filter(file -> file.getClass().equals(clazz))
                .map(file -> (T) file)
                .collect(Collectors.toList());
    }
}
