package org.fenixedu.bennu.io.domain;

import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
public final class FileStorageConfiguration extends FileStorageConfiguration_Base {
    private FileStorageConfiguration(final Class<? extends DomainObject> fileTypeClass) {
        setFileSupport(FileSupport.getInstance());
        setFileType(fileTypeClass.getName());
        setStorage(FileSupport.getInstance().getDefaultStorage());
    }

    @Override
    public String getFileType() {
        return super.getFileType();
    }

    public static FileStorage readFileStorageByFileType(final String fileType) {
        return FileSupport.getInstance().getConfigurationSet().stream()
                .filter(config -> config.getFileType().equals(fileType))
                .map(config -> config.getStorage())
                .findAny().orElse(null);
    }

    public static void createMissingStorageConfigurations() {
        final Map<String, FileStorageConfiguration> configs = new HashMap<>();
        for (final FileStorageConfiguration config : FileSupport.getInstance().getConfigurationSet()) {
            if (FenixFramework.getDomainModel().findClass(config.getFileType()) == null) {
                config.delete();
            } else {
                configs.put(config.getFileType(), config);
            }
        }

        for (final Class<? extends DomainObject> fileTypeClass : DomainModelUtil
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