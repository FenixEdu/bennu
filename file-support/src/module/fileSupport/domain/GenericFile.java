package module.fileSupport.domain;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
abstract public class GenericFile extends GenericFile_Base {

    public GenericFile() {
	super();
	this.setOjbConcreteClass(getClass().getName());
	this.setMyOrg(MyOrg.getInstance());
    }

    protected void init(String displayName, String filename, byte[] content) {
	setDisplayName(displayName);
	setFilename(filename);
	setContent(content);
    }

    public void setContent(byte[] content) {
	final FileStorage fileStorage = getFileStorage();
	fileStorage.store(getExternalId(), content);
	setStorage(fileStorage);
	setContentKey(getExternalId());
    }

    public byte[] getContent() {
	return getStorage().read(getExternalId());
    }

    private FileStorage getFileStorage() {
	final FileStorage fileStorage = FileStorageConfiguration.readFileStorageByFileType(getClass().getName());
	if (fileStorage == null) {
	    throw new DomainException("error.fileStorage.notDefinedForClassType");
	}
	return fileStorage;
    }
}
