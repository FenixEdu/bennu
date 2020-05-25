package org.fenixedu.bennu.io.domain;

public class DriveAPIFile extends DriveAPIFile_Base {
    
    public DriveAPIFile(final DriveAPIStorage driveAPIStorage, final String contentKey) {
        setDriveAPIStorageForFilesToDelete(driveAPIStorage);
        setContentKey(contentKey);;
    }

    public void delete() {
        setDriveAPIStorageForFilesToDelete(null);
        deleteDomainObject();
    }

}
