package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces.Indexable;

public class IndexingRequest extends IndexingRequest_Base {

    public IndexingRequest(Indexable indexableObject) {
        super();
        setIndexableExternalId(((DomainObject) indexableObject).getExternalId());
        setPluginRoot(LuceneSearchPluginRoot.getInstance());
    }

    public IndexDocument getIndex() {
        DomainObject someDomainObject = FenixFramework.getDomainObject(getIndexableExternalId());
        return ((Indexable) someDomainObject).getDocumentToIndex();
    }

    public void delete() {
        setPluginRoot(null);
        super.deleteDomainObject();
    }
}
