/*
 * @(#)PDFParser.java
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
package module.fileSupport.metadata.parsing.parsers;

import java.io.InputStream;
import java.io.StringWriter;

import module.fileSupport.metadata.parsing.FileMetaDataParser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class PDFParser extends FileMetaDataParser {

	@Override
	public boolean isAppliableTo(GenericFile file) {
		return file.getFilename().toLowerCase().endsWith(".pdf");
	}

	@Override
	protected String extract(InputStream stream) {
		StringWriter writer = null;
		try {
			PDDocument pdfDocument = PDDocument.load(stream);
			writer = new StringWriter();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText(pdfDocument, writer);
			pdfDocument.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return writer.toString();
	}
}
