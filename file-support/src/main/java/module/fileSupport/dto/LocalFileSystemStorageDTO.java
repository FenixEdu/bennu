/*
 * @(#)LocalFileSystemStorageDTO.java
 *
 * Copyright 2009 Instituto Superior Tecnico
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
package module.fileSupport.dto;

/**
 * 
 * @author Shezad Anavarali
 * 
 */
public class LocalFileSystemStorageDTO extends FileStorageDTO {

    String path;
    Integer treeDirectoriesNameLength;

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public Integer getTreeDirectoriesNameLength() {
	return treeDirectoriesNameLength;
    }

    public void setTreeDirectoriesNameLength(Integer treeDirectoriesNameLength) {
	this.treeDirectoriesNameLength = treeDirectoriesNameLength;
    }

}
