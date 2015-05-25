package org.fenixedu.bennu.io.domain;

import java.util.HashMap;
import java.util.Map;

import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

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
        setStorage(FileSupport.getInstance().getDefaultStorage());
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
        Map<String, FileStorageConfiguration> configs = new HashMap<>();
        for (FileStorageConfiguration config : FileSupport.getInstance().getConfigurationSet()) {
            if (FenixFramework.getDomainModel().findClass(config.getFileType()) == null) {
                config.delete();
            } else {
                configs.put(config.getFileType(), config);
            }
        }

        for (Class<? extends DomainObject> fileTypeClass : DomainModelUtil
                .getDomainClassHierarchy(GenericFile.class, true, false)) {
            if (!configs.containsKey(fileTypeClass.getName())) {
                new FileStorageConfiguration(fileTypeClass);
            }
        }
    }

    private void delete() {
        setFileSupport(null);
        setStorage(null);
        deleteDomainObject();
    }
}