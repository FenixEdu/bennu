package module.fileSupport.metadata.parsing.parsers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import module.fileSupport.domain.GenericFile;
import module.fileSupport.metadata.parsing.FileMetaDataParser;

public class TextParser extends FileMetaDataParser {

    @Override
    public boolean isAppliableTo(GenericFile file) {
	return file.getFilename().toLowerCase().endsWith(".txt");
    }

    @Override
    protected String extract(InputStream stream) {
	StringBuilder text = new StringBuilder();

	try {
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
	    String line = bufferedReader.readLine();
	    while (line != null) {
		text.append(line);
		text.append(" ");
		line = bufferedReader.readLine();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
	
	return text.toString();
    }
}
