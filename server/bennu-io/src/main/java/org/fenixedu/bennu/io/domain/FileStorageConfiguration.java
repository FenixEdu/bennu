package org.fenixedu.bennu.io.domain;

import java.util.HashSet;

import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixframework.DomainObject;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
public class FileStorageConfiguration extends FileStorageConfiguration_Base {
    public FileStorageConfiguration() {
        super();
        setFileSupport(FileSupport.getInstance());
    }

    public FileStorageConfiguration(Class<? extends DomainObject> fileTypeClass) {
        this();
        setFileType(fileTypeClass.getName());
    }

    public static FileStorage readFileStorageByFileType(final String fileType) {
        for (final FileStorageConfiguration fileStorageConfiguration : FileSupport.getInstance()
                .getFileStorageConfigurationsSet()) {
            if (fileStorageConfiguration.getFileType().equals(fileType)) {
                return fileStorageConfiguration.getStorage();
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static void createMissingStorageConfigurations() {
        final HashSet<String> existingFileTypes = new HashSet<>();
        for (final FileStorageConfiguration fileStorageConfiguration : FileSupport.getInstance()
                .getFileStorageConfigurationsSet()) {
            existingFileTypes.add(fileStorageConfiguration.getFileType());
        }

        for (Class<? extends DomainObject> fileTypeClass : DomainModelUtil
                .getDomainClassHierarchy(GenericFile.class, true, false)) {
            if (!existingFileTypes.contains(fileTypeClass.getName())) {
                new FileStorageConfiguration(fileTypeClass);
            }
        }

    }
}