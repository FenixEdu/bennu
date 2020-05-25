package org.fenixedu.bennu.scheduler.io;

import org.fenixedu.bennu.io.domain.DriveAPIStorage;
import org.fenixedu.bennu.io.domain.FileSupport;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "Delete Pending Remote Drive API Files Marked for Delete", readOnly = true)
public class DeletePendingRemoteDriveAPIFiles extends CronTask {
    @Override
    public void runTask() throws Exception {
        FileSupport.getInstance().getFileStorageSet().stream()
                .filter(storage -> storage instanceof DriveAPIStorage)
                .map(storage -> (DriveAPIStorage) storage)
                .forEach(DriveAPIStorage::deletePendingRemoteFiles);
    }
}
