/*
 * @(#)WordParser.java
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

import java.io.IOException;
import java.io.InputStream;

import module.fileSupport.metadata.parsing.FileMetaDataParser;

import org.apache.poi.hwpf.extractor.WordExtractor;

import pt.ist.bennu.io.domain.GenericFile;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class WordParser extends FileMetaDataParser {

    @Override
    public boolean isAppliableTo(GenericFile file) {
        return file.getFilename().toLowerCase().endsWith(".doc");
    }

    @Override
    protected String extract(InputStream stream) {

        String extractedText = null;
        try {
            WordExtractor extractor = new WordExtractor(stream);
            extractedText = extractor.getText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return extractedText;

    }
}
