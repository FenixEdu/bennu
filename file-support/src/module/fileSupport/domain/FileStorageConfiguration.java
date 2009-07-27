package module.fileSupport.domain;

import java.util.HashSet;

import myorg.domain.MyOrg;
import myorg.util.DomainModelUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.DomainObject;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, 2009
 * 
 */
public class FileStorageConfiguration extends FileStorageConfiguration_Base {

    public FileStorageConfiguration() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public FileStorageConfiguration(Class<? extends DomainObject> fileTypeClass) {
	this();
	setFileType(fileTypeClass.getName());
    }

    public static FileStorage readFileStorageByFileType(final String fileType) {
	for (final FileStorageConfiguration fileStorageConfiguration : MyOrg.getInstance().getFileStorageConfigurationsSet()) {
	    if (fileStorageConfiguration.getFileType().equals(fileType)) {
		return fileStorageConfiguration.getStorage();
	    }
	}
	return null;
    }

    @Service
    public static void createMissingStorageConfigurations() {

	final HashSet<String> existingFileTypes = new HashSet<String>();
	for (final FileStorageConfiguration fileStorageConfiguration : MyOrg.getInstance().getFileStorageConfigurations()) {
	    existingFileTypes.add(fileStorageConfiguration.getFileType());
	}

	for (Class<? extends DomainObject> fileTypeClass : DomainModelUtil
		.getDomainClassHierarchy(GenericFile.class, true, false)) {
	    if (!existingFileTypes.contains(fileTypeClass.getName())) {
		new FileStorageConfiguration(fileTypeClass);
	    }
	}

    }

}
