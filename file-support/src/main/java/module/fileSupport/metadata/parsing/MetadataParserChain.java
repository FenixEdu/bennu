/*
 * @(#)MetadataParserChain.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import module.fileSupport.metadata.parsing.parsers.ExcelParser;
import module.fileSupport.metadata.parsing.parsers.PDFParser;
import module.fileSupport.metadata.parsing.parsers.PowerPointParser;
import module.fileSupport.metadata.parsing.parsers.TextParser;
import module.fileSupport.metadata.parsing.parsers.WordParser;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class MetadataParserChain {
	public static final String TEXT_CONTENT_PROPERTY = "extractedContent";

	public static Set<FileMetaDataParser> parsers = new HashSet<FileMetaDataParser>();

	static {
		registerFileParser(new ExcelParser());
		registerFileParser(new PDFParser());
		registerFileParser(new PowerPointParser());
		registerFileParser(new TextParser());
		registerFileParser(new WordParser());
	}

	public static void registerFileParser(FileMetaDataParser parser) {
		parsers.add(parser);
	}

	public static <T extends GenericFile> FileMetadata parseFile(T file) {
		FileMetadata fileMetaData = new FileMetadata();
		for (FileMetaDataParser parser : parsers) {
			if (parser.isAppliableTo(file)) {
				parser.parse(file, fileMetaData);
			}
		}
		return fileMetaData;
	}

	public static FileMetadata parseFiles(List<? extends GenericFile> files) {
		FileMetadata fileMetaData = new FileMetadata();
		for (GenericFile file : files) {
			for (FileMetaDataParser parser : parsers) {
				if (parser.isAppliableTo(file)) {
					parser.parse(file, fileMetaData);
				}
			}
		}
		return fileMetaData;
	}

}
