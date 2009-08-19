package module.fileSupport.metadata.parsing.parsers;

import java.io.IOException;
import java.io.InputStream;

import module.fileSupport.domain.GenericFile;
import module.fileSupport.metadata.parsing.FileMetaDataParser;

import org.apache.poi.hwpf.extractor.WordExtractor;

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
