package myorg.domain.index;

import myorg.domain.MyOrg;

public class IndexDirectory extends IndexDirectory_Base {

    public IndexDirectory(String name) {
	super();
	setIndexForClass(name);
	setMyOrg(MyOrg.getInstance());
    }

    public IndexFile getIndexFile(String name) {
	for (IndexFile file : getIndexFiles()) {
	    if (file.getName().equals(name)) {
		return file;
	    }
	}
	return null;
    }

    public static IndexDirectory getIndexDirectory(String name) {
	for (IndexDirectory directory : MyOrg.getInstance().getIndexDirectory()) {
	    if (directory.getIndexForClass().equals(name)) {
		return directory;
	    }
	}
	return null;
    }

    public static IndexDirectory createNewIndexDirectory(String name) {
	return new IndexDirectory(name);
    }
}
