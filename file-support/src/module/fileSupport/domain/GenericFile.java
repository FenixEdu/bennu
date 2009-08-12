package module.fileSupport.domain;

import javax.activation.MimetypesFileTypeMap;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;
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

    private void setContent(byte[] content) {
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

    protected String guessContentType(final String filename) {
	return new MimetypesFileTypeMap().getContentType(filename);
    }

    @Service
    public void delete() {
	setContent(null);
	removeStorage();
	removeMyOrg();
	Transaction.deleteObject(this);
    }
}
