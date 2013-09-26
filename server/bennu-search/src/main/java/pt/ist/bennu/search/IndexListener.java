package pt.ist.bennu.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pt.ist.fenixframework.CommitListener;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.Transaction;
import pt.ist.fenixframework.txintrospector.TxIntrospector;

public class IndexListener implements CommitListener {
//    private static Logger logger = LoggerFactory.getLogger(IndexListener.class);

    @Override
    public void beforeCommit(Transaction transaction) {
        TxIntrospector introspector = transaction.getTxIntrospector();
        if (introspector.isWriteTransaction()) {
            Map<Class<? extends Indexable>, Collection<IndexDocument>> newIndexes = new HashMap<>();

            for (DomainObject domainObject : new HashSet<>(introspector.getNewObjects())) {
                if (domainObject instanceof Searchable) {
                    for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                        IndexDocument document = indexableObject.getDocumentToIndex();
                        if (!newIndexes.containsKey(document.getIndexableClass())) {
                            newIndexes.put(document.getIndexableClass(), new HashSet<IndexDocument>());
                        }
                        newIndexes.get(document.getIndexableClass()).add(document);
                    }
                }
            }

            for (DomainObject domainObject : introspector.getModifiedObjects()) {
                if (!introspector.isDeleted(domainObject)) {
                    if (domainObject instanceof Searchable) {
                        for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                            IndexDocument document = indexableObject.getDocumentToIndex();
                            if (!newIndexes.containsKey(document.getIndexableClass())) {
                                newIndexes.put(document.getIndexableClass(), new HashSet<IndexDocument>());
                            }
                            newIndexes.get(document.getIndexableClass()).add(document);
                        }
                    }
                }
            }

            transaction.putInContext("indexes", newIndexes);
        }
    }

    @Override
    public void afterCommit(Transaction transaction) {
        Map<Class<? extends Indexable>, Collection<IndexDocument>> newIndexes =
                (Map<Class<? extends Indexable>, Collection<IndexDocument>>) transaction.getFromContext("indexes");
        if (newIndexes != null && !newIndexes.isEmpty()) {
            DomainIndexer.getInstance().indexDomainObjects(newIndexes);
        }
    }
}
