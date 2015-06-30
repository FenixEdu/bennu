package org.fenixedu.bennu.io.domain;

import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public final class FileSupport extends FileSupport_Base {
    private FileSupport() {
        super();
        setBennu(Bennu.getInstance());
        setDefaultStorage(DomainStorage.createNewDomainStorage("system-default"));
    }

    public static FileSupport getInstance() {
        if (Bennu.getInstance().getFileSupport() == null) {
            return initialize();
        }
        return Bennu.getInstance().getFileSupport();
    }

    @Atomic(mode = TxMode.WRITE)
    private static FileSupport initialize() {
        if (Bennu.getInstance().getFileSupport() == null) {
            return new FileSupport();
        }
        return Bennu.getInstance().getFileSupport();
    }

    @Override
    public void setDefaultStorage(FileStorage defaultStorage) {
        super.setDefaultStorage(defaultStorage);
        addFileStorage(defaultStorage);
    }

    @Override
    public Set<FileStorage> getFileStorageSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getFileStorageSet();
    }

    @Override
    public Set<FileStorageConfiguration> getConfigurationSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getConfigurationSet();
    }
}
