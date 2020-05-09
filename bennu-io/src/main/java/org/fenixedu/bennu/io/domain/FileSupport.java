package org.fenixedu.bennu.io.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.Singleton;

import java.util.function.Supplier;

public final class FileSupport extends FileSupport_Base {

    private static final Supplier<FileSupport> SYSTEM_GETTER = () -> Bennu.getInstance().getFileSupport();
    private static final Supplier<FileSupport> SYSTEM_CREATOR = () -> new FileSupport();

    private FileSupport() {
        setBennu(Bennu.getInstance());
        setDefaultStorage(DomainStorage.createNewDomainStorage("system-default"));
    }

    public static FileSupport getInstance() {
        return Singleton.getInstance(SYSTEM_GETTER, SYSTEM_CREATOR);
    }

    @Override
    public void setDefaultStorage(final FileStorage defaultStorage) {
        super.setDefaultStorage(defaultStorage);
        addFileStorage(defaultStorage);
    }

}
