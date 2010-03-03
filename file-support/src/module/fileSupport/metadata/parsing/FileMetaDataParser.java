package module.fileSupport.metadata.parsing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

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
	    metaData.addMetaData(getPropertyName(), extractedText);
	}
    }

    protected String getPropertyName() {
	return getClass().getSimpleName() + ":extractedContent";
    }

    protected abstract String extract(InputStream stream);
}
