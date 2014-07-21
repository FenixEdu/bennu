package org.fenixedu.bennu.io.domain;

import java.io.InputStream;
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
import com.google.common.hash.Hashing;

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
        setChecksum(Hashing.sha1().hashBytes(content).toString());
        setChecksumAlgorithm("SHA");
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

    @Override
    public String getChecksum() {
        //FIXME: remove when the framework enables read-only slots
        return super.getChecksum();
    }

    @Override
    public String getChecksumAlgorithm() {
        //FIXME: remove when the framework enables read-only slots
        return super.getChecksumAlgorithm();
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
     * @return file content type
     * @deprecated content detection is done automatically, no need for this method
     */
    @Deprecated
    protected String guessContentType(final String filename) {
        return new MimetypesFileTypeMap().getContentType(filename);
    }

    /**
     * Detect content type based on file content "magic" bytes. Fallback to filename extension if file content is inconclusive.
     *
     * @return the detected mime-type. application/octet-stream returned when detection was not successful.
     *
     * @see Tika
     */
    protected String detectContentType(byte[] content, String filename) {
        return tika.detect(content, filename);
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
