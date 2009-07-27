package module.fileSupport.domain;

import myorg.domain.MyOrg;
import myorg.domain.util.ByteArray;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
public class FileContent extends FileContent_Base {

    public FileContent() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public FileContent(String uniqueIdentification, byte[] content) {
	this();
	setContent(new ByteArray(content));
	setContentKey(uniqueIdentification);
    }

    public static FileContent readByUniqueIdentification(final String uniqueIdentification) {
	for (final FileContent fileContent : MyOrg.getInstance().getFileContents()) {
	    if (fileContent.getContentKey().equals(uniqueIdentification)) {
		return fileContent;
	    }
	}
	return null;
    }

}
