package module.fileSupport.presentationTier.actions;

import module.fileSupport.dto.DBStorageDTO;
import module.fileSupport.dto.DomainStorageDTO;
import module.fileSupport.dto.LocalFileSystemStorageDTO;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileStorage;

public class ServiceHack {

    @Service
    public static void createDomainStorage(DomainStorageDTO domainStorageDTO) {
	FileStorage.createNewDomainStorage(domainStorageDTO.getName());
    }

    @Service
    public static void createDBStorage(DBStorageDTO dbStorageDTO) {
	FileStorage.createNewDatabaseStorage(dbStorageDTO.getName(), dbStorageDTO.getHost(), dbStorageDTO.getPort(), dbStorageDTO
		.getDbName(), dbStorageDTO.getTableName(), dbStorageDTO.getUsername(), dbStorageDTO.getPassword());
    }

    @Service
    public static void createLocalFileSystemStorage(LocalFileSystemStorageDTO fdStorageDTO) {
	FileStorage.createNewFileSystemStorage(fdStorageDTO.getName(), fdStorageDTO.getPath(), fdStorageDTO
		.getTreeDirectoriesNameLength());
    }
}
