package pt.ist.fenixframework.plugins.luceneIndexing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pt.ist.fenixframework.CommitListener;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.Transaction;
import pt.ist.fenixframework.plugins.LuceneSearchPlugin;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexingRequest;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces.Indexable;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces.Searchable;
import pt.ist.fenixframework.txintrospector.TxIntrospector;

public class IndexListener implements CommitListener {

    @Override
    public void afterCommit(Transaction topLevelTransaction) {

    }

    @Override
    public void beforeCommit(Transaction topLevelTransaction) {
        TxIntrospector introspector = topLevelTransaction.getTxIntrospector();
        if (introspector.isWriteTransaction()) {
            long t1 = System.currentTimeMillis();
            Map<Indexable, IndexDocument> newIndexes = new HashMap<Indexable, IndexDocument>();

            for (DomainObject domainObject : new HashSet<DomainObject>(introspector.getNewObjects())) {
                if (domainObject instanceof Searchable) {
                    for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                        switch (indexableObject.getIndexMode()) {
                        case ASYNC:
                            new IndexingRequest(indexableObject);
                            break;
                        case SYNC:
                            newIndexes.put(indexableObject, indexableObject.getDocumentToIndex());
                            break;
                        case MANUAL:
                            break;
                        }
                    }
                }
            }

            for (DomainObject domainObject : introspector.getModifiedObjects()) {
                if (!introspector.isDeleted(domainObject)) {
                    if (domainObject instanceof Searchable) {
                        for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                            switch (indexableObject.getIndexMode()) {
                            case ASYNC:
                                new IndexingRequest(indexableObject);
                                break;
                            case SYNC:
                                newIndexes.put(indexableObject, indexableObject.getDocumentToIndex());
                                break;
                            case MANUAL:
                                break;
                            }
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
