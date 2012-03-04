/*
 * @(#)FileMetaDataParser.java
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * 
 * @author Pedro Santos
 * @author Paulo Abrantes
 * 
 */
public abstract class FileMetaDataParser {
    public abstract boolean isAppliableTo(GenericFile file);

    public void parse(GenericFile file, FileMetadata metaData) {
	byte[] content = file.getContent();
	ByteArrayInputStream stream = new ByteArrayInputStream(content);
	String extractedText = null;
	try {
	    extractedText = extract(stream);
	} finally {
	    try {
		stream.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	if (!StringUtils.isEmpty(extractedText)) {
	    metaData.addMetaData(MetadataParserChain.TEXT_CONTENT_PROPERTY, extractedText);
	}
    }

    protected abstract String extract(InputStream stream);
}
