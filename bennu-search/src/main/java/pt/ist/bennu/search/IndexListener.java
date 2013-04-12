package pt.ist.bennu.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.CommitListener;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

public class IndexListener implements CommitListener {
    private static Logger logger = LoggerFactory.getLogger(IndexListener.class);

    @Override
    public void beforeCommit(TopLevelTransaction topLevelTransaction) {
    }

    @Override
    public void afterCommit(TopLevelTransaction topLevelTransaction) {
        if (topLevelTransaction.isWriteTransaction()) {
            long t1 = System.currentTimeMillis();
            Map<Class<? extends Indexable>, Collection<IndexDocument>> newIndexes = new HashMap<>();

            for (DomainObject domainObject : new HashSet<>(topLevelTransaction.getNewObjects())) {
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

            for (DomainObject domainObject : topLevelTransaction.getModifiedObjects()) {
                if (!topLevelTransaction.isDeleted(domainObject)) {
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

            if (!newIndexes.isEmpty()) {
                long t2 = System.currentTimeMillis();
                DomainIndexer.getInstance().indexDomainObjects(newIndexes);
                long t3 = System.currentTimeMillis();
                if (logger.isDebugEnabled()) {
                    for (Entry<Class<? extends Indexable>, Collection<IndexDocument>> entry : newIndexes.entrySet()) {
                        logger.debug("Indexed {} {} objects. Took {}ms. (tx fetch: {}, writing index: {})", entry.getValue()
                                .size(), entry.getKey().getSimpleName(), t3 - t1, t2 - t1, t3 - t2);
                        logger.debug("indexed " + newIndexes.size() + " objects in: " + (t3 - t1) + " (" + (t2 - t1) + ", "
                                + (t3 - t2) + ") ms.");
                    }
                }
            }
        }
    }
}
