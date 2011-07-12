package pt.ist.fenixframework.plugins.luceneIndexing;

import java.util.HashSet;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexingRequest;
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

	    for (DomainObject domainObject : new HashSet<DomainObject>(topLevelTransaction.getNewObjects())) {
		if (domainObject instanceof Searchable) {
		    for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
			new IndexingRequest(indexableObject);
		    }
		}
	    }

	    for (DomainObject domainObject : topLevelTransaction.getModifiedObjects()) {
		if (!topLevelTransaction.isDeleted(domainObject)) {
		    if (domainObject instanceof Searchable) {
			for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
			    new IndexingRequest(indexableObject);
			}
		    }
		}
	    }

	}
    }

}
