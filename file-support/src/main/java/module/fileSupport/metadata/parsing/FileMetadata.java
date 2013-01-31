/*
 * @(#)FileMetadata.java
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
package module.fileSupport.metadata.parsing;

import java.util.HashMap;
import java.util.Set;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class FileMetadata {

	private HashMap<String, String> metaData;

	public FileMetadata() {
		this.metaData = new HashMap<String, String>();
	}

	public void addMetaData(String propertyName, String value) {
		String currentValue = this.metaData.get(propertyName);
		if (currentValue == null) {
			this.metaData.put(propertyName, value);
		} else {
			StringBuilder builder = new StringBuilder(currentValue);
			builder.append(" ");
			builder.append(value);
			this.metaData.remove(propertyName);
			this.metaData.put(propertyName, builder.toString());
		}

	}

	public void addMetaData(FileMetadata data) {
		metaData.putAll(data.metaData);
	}

	public HashMap<String, String> getMetaDataMap() {
		return this.metaData;
	}

	public boolean hasContent() {
		return !this.metaData.isEmpty();
	}

	public Set<String> keySet() {
		return this.metaData.keySet();
	}

	public String getObject(String propertyName) {
		return this.metaData.get(propertyName);
	}
}
