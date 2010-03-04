package pt.ist.fenixframework.plugins.fileSupport.domain;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import jvstm.TransactionalCommand;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
abstract public class GenericFile extends GenericFile_Base {

    public GenericFile() {
	super();
	this.setOjbConcreteClass(getClass().getName());
	setFileSupport(FileSupport.getInstance());
    }

    protected void init(String displayName, String filename, byte[] content) {
	if (content == null) {
	    throw new NullPointerException("Content byte[] is null");
	}
	setDisplayName(displayName);
	setFilename(filename);
	setContent(content);
    }

    @Override
    public void setFilename(String filename) {
	final String nicerFilename = FilenameUtils.getName(filename);
	final String normalizedFilename = Normalizer.normalize(nicerFilename, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	super.setFilename(normalizedFilename);
	super.setContentType(guessContentType(normalizedFilename));
    }

    public void setContent(byte[] content) {
	final FileStorage fileStorage = getFileStorage();
	final String uniqueIdentification = fileStorage.store(StringUtils.isEmpty(getContentKey()) ? getExternalId()
		: getContentKey(), content);
	setStorage(fileStorage);

	if (StringUtils.isEmpty(uniqueIdentification) && content != null) {
	    throw new RuntimeException();
	}

	setContentKey(uniqueIdentification);
    }

    public byte[] getContent() {
	return getStorage().read(getContentKey());
    }

    private void updateFileStorage() {
	Transaction.withTransaction(new TransactionalCommand() {

	    @Override
	    public void doIt() {
		setContent(getContent());
	    }
	});
    }

    public static void convertFileStorages(final FileStorage fileStorageToUpdate) {

	if (fileStorageToUpdate != null) {
	    try {
		for (final GenericFile genericFile : FileSupport.getInstance().getGenericFiles()) {
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
	    throw new RuntimeException("error.fileStorage.notDefinedForClassType");
	}
	return fileStorage;
    }

    protected String guessContentType(final String filename) {
	return new MimetypesFileTypeMap().getContentType(filename);
    }

    public void deleteService() {
	Transaction.withTransaction(new TransactionalCommand() {

	    @Override
	    public void doIt() {
		delete();
	    }

	});

    }

    public void delete() {
	setContent(null);
	removeStorage();
	removeFileSupport();
	deleteDomainObject();
    }

    public static <T extends GenericFile> List<T> getFiles(final Class<T> clazz) {
	final List<T> files = new ArrayList<T>();
	for (final GenericFile file : FileSupport.getInstance().getGenericFiles()) {
	    if (file.getClass().equals(clazz)) {
		files.add((T) file);
	    }
	}
	return files;
    }
}