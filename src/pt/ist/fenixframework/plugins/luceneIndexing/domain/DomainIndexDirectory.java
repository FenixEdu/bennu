package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import java.io.IOException;

import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DomainIndexException;
import pt.ist.fenixframework.plugins.luceneIndexing.LuceneDomainDirectory;

public class DomainIndexDirectory extends DomainIndexDirectory_Base {

    public DomainIndexDirectory(String name) {
	super();
	setPluginRoot(LuceneSearchPluginRoot.getInstance());
	setIndexForClass(name);
    }

    public DomainIndexFile getIndexFile(String name) {
	for (DomainIndexFile file : getIndexFiles()) {
	    if (file.getName().equals(name) && file.getIndexContent() != null) {
		return file;
	    }
	}
	return null;
    }

    public LuceneDomainDirectory getLuceneDomainDirectory() {
	try {
	    return new LuceneDomainDirectory(this);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	}
    }

    public static boolean hasIndexDirectory(String name) {
	return getIndexDirectory(name) != null;
    }

    public static DomainIndexDirectory getIndexDirectory(String name) {
	return getIndexDirectory(name, false);
    }

    public static DomainIndexDirectory getIndexDirectory(String name, boolean create) {
	for (DomainIndexDirectory directory : LuceneSearchPluginRoot.getInstance().getDomainIndexDirectories()) {
	    if (directory.getIndexForClass().equals(name)) {
		return directory;
	    }
	}
	if (create) {
	    return new DomainIndexDirectory(name);
	}
	return null;
    }

    public void delete() {
	for (DomainIndexFile file : getIndexFilesSet()) {
	    file.delete();
	}
	removePluginRoot();
	deleteDomainObject();
    }
}
