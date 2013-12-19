package org.fenixedu.bennu.io.domain;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Strings;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
abstract public class GenericFile extends GenericFile_Base {
    private static final Logger logger = LoggerFactory.getLogger(GenericFile.class);

    public GenericFile() {
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

    @Override
    public void setFilename(String filename) {
        final String nicerFilename = filename.substring(filename.lastIndexOf('/') + 1);
        final String normalizedFilename = Normalizer.normalize(nicerFilename, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        super.setFilename(normalizedFilename);
        super.setContentType(guessContentType(normalizedFilename));
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
    }

    public byte[] getContent() {
        return getStorage().read(getContentKey());
    }

    public InputStream getStream() {
        return getStorage().readAsInputStream(getContentKey());
    }

    /**
     * 
     * @param algorithm
     *            the String identifying the algorithm to use. This method uses
     *            the {@link MessageDigest} class, thus you should take a look
     *            at {@linkplain http
     *            ://download.oracle.com/javase/6/docs/technotes
     *            /guides/security/StandardNames.html#MessageDigest} for more
     *            information
     */
    public byte[] getMessageDigest(String algorithm) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return messageDigest.digest(getContent());
    }

    /**
     * Convenience method
     * 
     * @return the SHA-256 digest of the file content
     */
    public byte[] getSHA256MessageDigest() {
        return getMessageDigest("SHA-256");
    }

    public byte[] getSHA1MessageDigest() {
        return getMessageDigest("SHA-1");
    }

    /**
     * Convenience method for {@link #getSHA256MessageDigest()} to retrieve the
     * content in hexadecimal
     * 
     * @return hexadecimal representation of the SHA256 digest
     */
    public String getHexSHA256MessageDigest() {
        return Hex.encodeHexString(getSHA256MessageDigest());

    }

    /**
     * Convenience method for {@link #getSHA1MessageDigest()} to retrieve the
     * content in hexadecimal
     * 
     * @return hexadecimal representation of the SHA1 digest
     */
    public String getHexSHA1MessageDigest() {
        return Hex.encodeHexString(getSHA1MessageDigest());
    }

    public static void convertFileStorages(final FileStorage fileStorageToUpdate) {
        if (fileStorageToUpdate != null) {
            try {
                for (final GenericFile genericFile : FileSupport.getInstance().getGenericFilesSet()) {
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
            throw new RuntimeException("error.fileStorage.notDefinedForClassType");
        }
        return fileStorage;
    }

    protected String guessContentType(final String filename) {
        return new MimetypesFileTypeMap().getContentType(filename);
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
        for (final GenericFile file : FileSupport.getInstance().getGenericFilesSet()) {
            if (file.getClass().equals(clazz)) {
                files.add((T) file);
            }
        }
        return files;
    }
}
