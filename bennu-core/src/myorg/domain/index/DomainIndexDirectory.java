package myorg.domain.index;

import myorg.domain.MyOrg;

public class DomainIndexDirectory extends DomainIndexDirectory_Base {
    
    public DomainIndexDirectory(String name) {
	super();
	setIndexForClass(name);
	setMyOrg(MyOrg.getInstance());
    }

    public DomainIndexFile getIndexFile(String name) {
	for (DomainIndexFile file : getIndexFiles()) {
	    if (file.getName().equals(name)) {
		return file;
	    }
	}
	return null;
    }

    public static DomainIndexDirectory getIndexDirectory(String name) {
	for (DomainIndexDirectory directory : MyOrg.getInstance().getDomainIndexDirectories()) {
	    if (directory.getIndexForClass().equals(name)) {
		return directory;
	    }
	}
	return null;
    }

    public static DomainIndexDirectory createNewIndexDirectory(String name) {
	return new DomainIndexDirectory(name);
    }
    
}
