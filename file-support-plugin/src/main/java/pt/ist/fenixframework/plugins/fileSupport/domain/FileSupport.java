package pt.ist.fenixframework.plugins.fileSupport.domain;

import pt.ist.fenixframework.FenixFramework;

public class FileSupport extends FileSupport_Base {

    private static FileSupport instance = null;

    private FileSupport() {
        super();
        FileSupport root = FenixFramework.getDomainRoot().getFileSupport();
        if (root != null && root != this) {
            throw new Error("Trying to create a 2nd instance of FileSupport! That, my friend, is not allowed!");
        }

    }

    public static FileSupport getInstance() {
        if (instance == null) {
            instance = FenixFramework.getDomainRoot().getFileSupport();
            if (instance == null) {
                instance = new FileSupport();
                FenixFramework.getDomainRoot().setFileSupport(instance);
            }
        }
        return instance;
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.fileSupport.domain.FileRawData> getFileRawDatas() {
        return getFileRawDatasSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.fileSupport.domain.FileStorage> getFileStorages() {
        return getFileStoragesSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.fileSupport.domain.LocalFileToDelete> getLocalFilesToDelete() {
        return getLocalFilesToDeleteSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.fileSupport.domain.FileStorageConfiguration> getFileStorageConfigurations() {
        return getFileStorageConfigurationsSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile> getGenericFiles() {
        return getGenericFilesSet();
    }

}
