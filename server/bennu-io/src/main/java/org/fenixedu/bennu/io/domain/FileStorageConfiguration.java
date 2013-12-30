package org.fenixedu.bennu.io.domain;

import java.util.HashSet;

import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixframework.DomainObject;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
public final class FileStorageConfiguration extends FileStorageConfiguration_Base {
    private FileStorageConfiguration(Class<? extends DomainObject> fileTypeClass) {
        super();
        setFileSupport(FileSupport.getInstance());
        setFileType(fileTypeClass.getName());
    }

    @Override
    public String getFileType() {
        return super.getFileType();
    }

    public static FileStorage readFileStorageByFileType(final String fileType) {
        for (final FileStorageConfiguration fileStorageConfiguration : FileSupport.getInstance().getConfigurationSet()) {
            if (fileStorageConfiguration.getFileType().equals(fileType)) {
                return fileStorageConfiguration.getStorage();
            }
        }
        return null;
    }

    public static void createMissingStorageConfigurations() {
        final HashSet<String> existingFileTypes = new HashSet<>();
        for (final FileStorageConfiguration fileStorageConfiguration : FileSupport.getInstance().getConfigurationSet()) {
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