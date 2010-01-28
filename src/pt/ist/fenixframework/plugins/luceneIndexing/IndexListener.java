package pt.ist.fenixframework.plugins.luceneIndexing;

import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces.Indexable;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces.Searchable;
import pt.ist.fenixframework.pstm.CommitListener;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

public class IndexListener implements CommitListener {

    @Override
    public void afterCommit(TopLevelTransaction topLevelTransaction) {

    }

    @Override
    public void beforeCommit(TopLevelTransaction topLevelTransaction) {
	if (topLevelTransaction.isWriteTransaction()) {
	    final Set<IndexDocument> documentsToIndex = new HashSet<IndexDocument>();

	    for (DomainObject domainObject : new HashSet<DomainObject>(topLevelTransaction.getNewObjects())) {
		if (domainObject instanceof Searchable) {
		    for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
			documentsToIndex.add(indexableObject.getDocumentToIndex());
		    }
		}
	    }

	    for (DomainObject domainObject : topLevelTransaction.getModifiedObjects()) {
		if (domainObject instanceof Searchable) {
		    for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
			documentsToIndex.add(indexableObject.getDocumentToIndex());
		    }
		}
	    }
	    DomainIndexer.getInstance().indexDomainObjects(documentsToIndex);

	}
    }

}
