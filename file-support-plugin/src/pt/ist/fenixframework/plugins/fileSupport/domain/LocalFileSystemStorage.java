package pt.ist.fenixframework.plugins.fileSupport.domain;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

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

    public LocalFileSystemStorage(String name, String path, Integer treeDirectoriesNameLength) {
	this();
	setName(name);
	setPath(path);
	setTreeDirectoriesNameLength(treeDirectoriesNameLength);
    }

    @Override
    public String store(String uniqueIdentification, byte[] content) {

	final String fullPath = getFullPath(uniqueIdentification);
	try {

	    if (content == null) {
		new LocalFileToDelete(fullPath + uniqueIdentification);
	    } else {
		FileUtils.forceMkdir(new File(fullPath));
		FileUtils.writeByteArrayToFile(new File(fullPath + uniqueIdentification), content);
	    }
	    return uniqueIdentification;

	} catch (IOException e) {
	    throw new RuntimeException("error.store.file", e);
	}
    }

    private String getFullPath(final String uniqueIdentification) {
	final String path = getPath().endsWith(PATH_SEPARATOR) ? getPath() : getPath() + PATH_SEPARATOR;
	return getPhysicalPath(path) + transformIDInPath(uniqueIdentification) + PATH_SEPARATOR;
    }

    private String getPhysicalPath(final String path) {
	if (path.indexOf("{") != -1 && path.indexOf("}") != -1) {
	    // Compile regular expression
	    Matcher matcher = Pattern.compile("(\\{.+?\\})").matcher(path);

	    // Replace all occurrences of pattern in input
	    StringBuffer result = new StringBuffer();
	    boolean found = false;
	    while ((found = matcher.find())) {
		String replaceStr = StringUtils.strip(matcher.group(), "{}");
		matcher.appendReplacement(result, System.getProperty(replaceStr));
	    }
	    matcher.appendTail(result);

	    return result.toString();
	}
	return path;
    }

    private String transformIDInPath(final String uniqueIdentification) {
	final Integer directoriesNameLength = getTreeDirectoriesNameLength();

	final StringBuilder result = new StringBuilder();

	char[] idArray = uniqueIdentification.toCharArray();
	for (int i = 0; i < idArray.length; i++) {
	    if (i > 0 && i % directoriesNameLength == 0 && ((i + directoriesNameLength) < uniqueIdentification.length())) {
		result.append(PATH_SEPARATOR);
	    } else if ((i + directoriesNameLength) >= uniqueIdentification.length()) {
		break;
	    }
	    result.append(idArray[i]);
	}

	return result.toString();
    }

    @Override
    public byte[] read(String uniqueIdentification) {
	try {
	    return FileUtils.readFileToByteArray(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
	} catch (IOException e) {
	    throw new RuntimeException("error.store.file", e);
	}
    }

    // @Override
    // public Collection<Pair<String, String>> getPresentationDetails() {
    // List<Pair<String, String>> result = new ArrayList<Pair<String,
    // String>>();
    // result.add(new Pair<String,
    // String>(BundleUtil.getStringFromResourceBundle("resources.FileSupportResources",
    // "label.localFileSystemStorage.path"), getPath()));
    // result.add(new Pair<String,
    // String>(BundleUtil.getStringFromResourceBundle("resources.FileSupportResources",
    // "label.localFileSystemStorage.treeDirectoriesNameLength"),
    // getTreeDirectoriesNameLength() != null ?
    // getTreeDirectoriesNameLength().toString() : ""));
    // return result;
    // }

}