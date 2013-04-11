package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import pt.ist.fenixframework.FenixFramework;

public class LuceneSearchPluginRoot extends LuceneSearchPluginRoot_Base {

    private static LuceneSearchPluginRoot instance = null;

    private LuceneSearchPluginRoot() {
        super();
        LuceneSearchPluginRoot root = FenixFramework.getDomainRoot().getLuceneSearchPluginRoot();
        if (root != null && root != this) {
            throw new Error("Trying to create a 2nd instance of LuceneSearchPluginRoot! That, my friend, is not allowed!");
        }

    }

    public static LuceneSearchPluginRoot getInstance() {
        if (instance == null) {
            instance = FenixFramework.getDomainRoot().getLuceneSearchPluginRoot();
            if (instance == null) {
                instance = new LuceneSearchPluginRoot();
                FenixFramework.getDomainRoot().setLuceneSearchPluginRoot(instance);
            }
        }
        return instance;
    }
    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexFile> getDomainIndexFiles() {
        return getDomainIndexFilesSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexingRequest> getIndexingRequests() {
        return getIndexingRequestsSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexDirectory> getDomainIndexDirectories() {
        return getDomainIndexDirectoriesSet();
    }

}
