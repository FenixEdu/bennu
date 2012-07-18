/*
 * @(#)ServiceHack.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Shezad Anavarali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the File Support Module.
 *
 *   The File Support Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The File Support Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the File Support Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.fileSupport.presentationTier.actions;

import module.fileSupport.dto.DBStorageDTO;
import module.fileSupport.dto.DomainStorageDTO;
import module.fileSupport.dto.LocalFileSystemStorageDTO;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileStorage;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
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
