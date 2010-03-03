package module.fileSupport.metadata.parsing.parsers;

import java.io.IOException;
import java.io.InputStream;

import module.fileSupport.metadata.parsing.FileMetaDataParser;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

public class ExcelParser extends FileMetaDataParser {

    @Override
    public boolean isAppliableTo(GenericFile file) {
	return file.getFilename().toLowerCase().endsWith(".ppt");
    }

    @Override
    protected String extract(InputStream stream) {
	String extractedText = null;
	try {
	    ExcelExtractor extractor = new ExcelExtractor(new HSSFWorkbook(stream));
	    extractedText = extractor.getText();
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}

	return extractedText;

    }

}
