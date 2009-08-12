package module.fileSupport.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import module.fileSupport.dto.LocalFileSystemStorageDTO;
import myorg.domain.exceptions.DomainException;
import myorg.util.BundleUtil;

import org.apache.commons.lang.StringUtils;

import pt.utl.ist.fenix.tools.util.FileUtils;
import pt.utl.ist.fenix.tools.util.Pair;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public class LocalFileSystemStorage extends LocalFileSystemStorage_Base {

    private static final String PATH_SEPARATOR = "/";

    public LocalFileSystemStorage() {
	super();
    }

    public LocalFileSystemStorage(LocalFileSystemStorageDTO storageDTO) {
	this();
	setName(storageDTO.getName());
	setPath(storageDTO.getPath());
	setTreeDirectoriesNameLength(storageDTO.getTreeDirectoriesNameLength());
    }

    @Override
    public void store(String uniqueIdentification, byte[] content) {

	final String fullPath = getFullPath(uniqueIdentification);
	try {

	    if (content == null) {
		new LocalFileToDelete(fullPath + uniqueIdentification);
	    } else {
		FileUtils.createDir(fullPath);
		FileUtils.writeFile(fullPath + uniqueIdentification, content, false);
	    }

	} catch (IOException e) {
	    throw new DomainException("error.store.file", e);
	}
    }

    private String getFullPath(final String uniqueIdentification) {
	return (getPath().endsWith(PATH_SEPARATOR) ? getPath() : getPath() + PATH_SEPARATOR)
		+ transformIDInPath(uniqueIdentification) + PATH_SEPARATOR;
    }

    private String transformIDInPath(final String uniqueIdentification) {
	final String reversedId = StringUtils.reverse(uniqueIdentification);
	final Integer directoriesNameLength = getTreeDirectoriesNameLength();

	final StringBuilder result = new StringBuilder();

	char[] idArray = reversedId.toCharArray();
	for (int i = 0; i < idArray.length; i++) {
	    if (i > 0 && i % directoriesNameLength == 0) {
		result.append(PATH_SEPARATOR);
	    }
	    result.append(idArray[i]);
	}

	return result.toString();
    }

    @Override
    public byte[] read(String uniqueIdentification) {
	try {
	    return FileUtils.readFileInBytes(getFullPath(uniqueIdentification) + uniqueIdentification);
	} catch (IOException e) {
	    throw new DomainException("error.store.file", e);
	}
    }

    @Override
    public Collection<Pair<String, String>> getPresentationDetails() {
	List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
	result.add(new Pair<String, String>(BundleUtil.getStringFromResourceBundle("resources.FileSupportResources",
		"label.localFileSystemStorage.path"), getPath()));
	result.add(new Pair<String, String>(BundleUtil.getStringFromResourceBundle("resources.FileSupportResources",
		"label.localFileSystemStorage.treeDirectoriesNameLength"),
		getTreeDirectoriesNameLength() != null ? getTreeDirectoriesNameLength().toString() : ""));
	return result;
    }

}
