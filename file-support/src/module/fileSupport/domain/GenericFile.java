package module.fileSupport.domain;

import javax.activation.MimetypesFileTypeMap;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;
import myorg.domain.util.ByteArray;
import pt.utl.ist.fenix.tools.util.FileUtils;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

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

    @Override
    public void setFilename(String filename) {
	final String nicerFilename = FileUtils.getFilenameOnly(filename);
	final String normalizedFilename = StringNormalizer.normalizePreservingCapitalizedLetters(nicerFilename);
	super.setFilename(normalizedFilename);
	super.setContentType(guessContentType(normalizedFilename));
    }

    public void setContent(byte[] content) {
	final FileStorage fileStorage = getFileStorage();
	fileStorage.store(getExternalId(), content);
	setStorage(fileStorage);
	setContentKey(getExternalId());
    }
    
    public void setContent(final ByteArray content) {
	setContent(content != null ? content.getBytes() : null);
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

    protected String guessContentType(final String filename) {
	return new MimetypesFileTypeMap().getContentType(filename);
    }

}
