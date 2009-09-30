package module.fileSupport.domain;

import java.io.File;

import myorg.domain.MyOrg;

/**
 * 
 * @author Shezad Anavarali Date: Aug 12, 2009
 * 
 */
public class LocalFileToDelete extends LocalFileToDelete_Base {

    public LocalFileToDelete() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public LocalFileToDelete(String path) {
	this();
	setFilePath(path);
    }

    public void delete() {
	final File existingFile = new File(getFilePath());
	if (!existingFile.exists() || existingFile.delete()) {
	    deleteDomainObject();
	}
    }

}
