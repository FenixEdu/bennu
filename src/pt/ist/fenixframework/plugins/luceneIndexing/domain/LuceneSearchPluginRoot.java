package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import pt.ist.fenixframework.pstm.PersistentRoot;

public class LuceneSearchPluginRoot extends LuceneSearchPluginRoot_Base {

    private static LuceneSearchPluginRoot instance = null;

    private LuceneSearchPluginRoot() {
	super();
	LuceneSearchPluginRoot root = PersistentRoot.getRoot(LuceneSearchPluginRoot.class.getName());
	if (root != null && root != this) {
	    throw new Error("Trying to create a 2nd instance of LuceneSearchPluginRoot! That, my friend, is not allowed!");
	}

    }

    public static LuceneSearchPluginRoot getInstance() {
	if (instance == null) {
	    instance = PersistentRoot.getRoot(LuceneSearchPluginRoot.class.getName());
	    if (instance == null) {
		instance = new LuceneSearchPluginRoot();
		PersistentRoot.addRoot(LuceneSearchPluginRoot.class.getName(), instance);
	    }
	}
	return instance;
    }
}
