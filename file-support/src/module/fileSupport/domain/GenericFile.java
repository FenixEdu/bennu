package module.fileSupport.domain;

import myorg.domain.MyOrg;

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
	fileStorage.store(String.valueOf(getOid()), content);
	setStorage(fileStorage);
	setContentKey(String.valueOf(getOid()));
    }

    public byte[] getContent() {
	return getStorage().read(String.valueOf(getOid()));
    }

    private FileStorage getFileStorage() {
	return FileStorageConfiguration.readFileStorageByFileType(getClass().getName());
	// FIXME: get default fileStorage if(fileStorage == null)
    }
}
