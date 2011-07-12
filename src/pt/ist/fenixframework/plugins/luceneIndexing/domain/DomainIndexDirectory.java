package pt.ist.fenixframework.plugins.luceneIndexing.domain;

public class DomainIndexDirectory extends DomainIndexDirectory_Base {

    public DomainIndexDirectory(String name) {
	super();
	setPluginRoot(LuceneSearchPluginRoot.getInstance());
	setIndexForClass(name);
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
	for (DomainIndexDirectory directory : LuceneSearchPluginRoot.getInstance().getDomainIndexDirectories()) {
	    if (directory.getIndexForClass().equals(name)) {
		return directory;
	    }
	}
	return null;
    }

    public static DomainIndexDirectory createNewIndexDirectory(String name) {
	return new DomainIndexDirectory(name);
    }

    public void delete() {
	for (DomainIndexFile file : getIndexFilesSet()) {
	    file.delete();
	}
	removePluginRoot();
	deleteDomainObject();
    }
}
