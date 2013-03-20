package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces.Indexable;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

public class IndexingRequest extends IndexingRequest_Base {

    public IndexingRequest(Indexable indexableObject) {
        super();
        setIndexableExternalId(((AbstractDomainObject) indexableObject).getExternalId());
        setPluginRoot(LuceneSearchPluginRoot.getInstance());
    }

    public IndexDocument getIndex() {
        AbstractDomainObject someDomainObject = AbstractDomainObject.fromExternalId(getIndexableExternalId());
        return ((Indexable) someDomainObject).getDocumentToIndex();
    }

    public void delete() {
        removePluginRoot();
        super.deleteDomainObject();
    }
}
