package module.fileSupport.domain;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.Service;
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
	final String uniqueIdentification = fileStorage.store(StringUtils.isEmpty(getContentKey()) ? getExternalId()
		: getContentKey(), content);
	setStorage(fileStorage);

	if (StringUtils.isEmpty(uniqueIdentification) && content != null) {
	    throw new DomainException();
	}

	setContentKey(uniqueIdentification);
    }

    public byte[] getContent() {
	return getStorage().read(getContentKey());
    }

    @Service
    private void updateFileStorage() {
	setContent(getContent());
    }

    public static void convertFileStorages(final FileStorage fileStorageToUpdate) {

	if (fileStorageToUpdate != null) {
	    try {
		for (final GenericFile genericFile : MyOrg.getInstance().getGenericFiles()) {
		    if (fileStorageToUpdate == genericFile.getFileStorage() && fileStorageToUpdate != genericFile.getStorage()) {
			genericFile.updateFileStorage();
		    }
		}
		System.out.println("FILE Conversion: DONE SUCESSFULLY!");
	    } catch (Throwable e) {
		System.out.println("FILE Conversion: ABORTED!!!");
		e.printStackTrace();
	    }
	}
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
    public void deleteService() {
	delete();
    }

    public void delete() {
	setContent(null);
	removeStorage();
	removeMyOrg();
	deleteDomainObject();
    }

    public static <T extends GenericFile> List<T> getFiles(final Class<T> clazz) {
	final List<T> files = new ArrayList<T>();
	for (final GenericFile file : MyOrg.getInstance().getGenericFiles()) {
	    if (file.getClass().equals(clazz)) {
		files.add((T) file);
	    }
	}
	return files;
    }
}
