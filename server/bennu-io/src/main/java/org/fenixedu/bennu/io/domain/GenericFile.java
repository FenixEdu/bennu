package org.fenixedu.bennu.io.domain;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.fenixedu.commons.StringNormalizer;
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
public abstract class GenericFile extends GenericFile_Base {
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
    public void setFilename(String filename) {
        final String nicerFilename = filename.substring(filename.lastIndexOf('/') + 1);
        final String normalizedFilename = StringNormalizer.normalizePreservingCapitalizedLetters(nicerFilename);
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
        for (final GenericFile file : FileSupport.getInstance().getFileSet()) {
            if (file.getClass().equals(clazz)) {
                files.add((T) file);
            }
        }
        return files;
    }
}
