package module.fileSupport.metadata.parsing.parsers;

import java.io.InputStream;
import java.io.StringWriter;

import module.fileSupport.domain.GenericFile;
import module.fileSupport.metadata.parsing.FileMetaDataParser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

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
