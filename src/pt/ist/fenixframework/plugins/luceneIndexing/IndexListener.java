package pt.ist.fenixframework.plugins.luceneIndexing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.LuceneSearchPlugin;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
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
	    long t1 = System.currentTimeMillis();
	    Map<Indexable, IndexDocument> newIndexes = new HashMap<Indexable, IndexDocument>();

	    for (DomainObject domainObject : new HashSet<DomainObject>(topLevelTransaction.getNewObjects())) {
		if (domainObject instanceof Searchable) {
		    for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
			newIndexes.put(indexableObject, indexableObject.getDocumentToIndex());
		    }
		}
	    }

	    for (DomainObject domainObject : topLevelTransaction.getModifiedObjects()) {
		if (!topLevelTransaction.isDeleted(domainObject)) {
		    if (domainObject instanceof Searchable) {
			for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
			    newIndexes.put(indexableObject, indexableObject.getDocumentToIndex());
			}
		    }
		}
	    }

	    if (!newIndexes.isEmpty()) {
		long t2 = System.currentTimeMillis();
		if (newIndexes.size() > 500) {
		    for (Indexable indexable : newIndexes.keySet()) {
			new IndexingRequest(indexable);
		    }
		} else {
		    DomainIndexer.getInstance().indexDomainObjects(newIndexes.values());
		}
		long t3 = System.currentTimeMillis();
		LuceneSearchPlugin.LOGGER.info("indexed " + newIndexes.size() + " objects in: " + (t3 - t1) + " (" + (t2 - t1)
			+ ", " + (t3 - t2) + ") ms.");
	    }
	}
    }
}
