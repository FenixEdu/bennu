package pt.ist.bennu.search;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.Status;
import javax.transaction.SystemException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.search.DomainIndexer.DefaultIndexFields;
import pt.ist.bennu.search.DomainIndexer.DomainIndexException;
import pt.ist.fenixframework.CommitListener;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.Transaction;
import pt.ist.fenixframework.txintrospector.TxIntrospector;

public class IndexListener implements CommitListener {
    private static Logger logger = LoggerFactory.getLogger(IndexListener.class);

    @Override
    @SuppressWarnings("resource")
    public void beforeCommit(Transaction transaction) {
        TxIntrospector introspector = transaction.getTxIntrospector();
        if (introspector.isWriteTransaction()) {
            try {
                int indexed = 0;
                int unindexed = 0;
                Map<Class<? extends Indexable>, IndexWriter> writers = new HashMap<>();
                for (DomainObject domainObject : new HashSet<>(introspector.getNewObjects())) {
                    if (domainObject instanceof Searchable) {
                        for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                            IndexDocument document = indexableObject.getDocumentToIndex();
                            getWriterFor(writers, indexableObject.getClass()).addDocument(document.getLuceneDocument());
                            indexed++;
                        }
                    }
                }
                for (DomainObject domainObject : introspector.getModifiedObjects()) {
                    if (domainObject instanceof Searchable) {
                        if (!introspector.isDeleted(domainObject)) {
                            for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                                IndexDocument document = indexableObject.getDocumentToIndex();
                                IndexWriter writer = getWriterFor(writers, indexableObject.getClass());
                                writer.deleteDocuments(new Term(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), domainObject
                                        .getExternalId()));
                                writer.addDocument(document.getLuceneDocument());
                                indexed++;
                            }
                        } else {
                            IndexWriter writer = getWriterFor(writers, (Class<? extends Indexable>) domainObject.getClass());
                            writer.deleteDocuments(new Term(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), domainObject
                                    .getExternalId()));
                            unindexed++;
                        }
                    }
                }
                Set<Indexable> reindex = FenixFramework.getTransaction().getFromContext(DomainIndexer.FORCE_REINDEX_LIST);
                if (reindex != null) {
                    for (Indexable indexableObject : reindex) {
                        IndexDocument document = indexableObject.getDocumentToIndex();
                        IndexWriter writer = getWriterFor(writers, indexableObject.getClass());
                        writer.deleteDocuments(new Term(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), indexableObject
                                .getExternalId()));
                        writer.addDocument(document.getLuceneDocument());
                        indexed++;
                    }
                }
                if (!writers.isEmpty()) {
                    for (IndexWriter writer : writers.values()) {
                        writer.prepareCommit();
                    }
                    transaction.putInContext("writers", writers.values());
                    transaction.putInContext("indexed", indexed);
                    transaction.putInContext("unindexed", unindexed);
                }
            } catch (IOException e) {
                logger.error("Before commit: something crashed", e);
            }
        }
    }

    @Override
    public void afterCommit(Transaction transaction) {
        Collection<IndexWriter> writers = transaction.getFromContext("writers");
        if (writers != null && !writers.isEmpty()) {
            try {
                Integer indexed = transaction.getFromContext("indexed");
                Integer unindexed = transaction.getFromContext("unindexed");
                if (transaction.getStatus() == Status.STATUS_COMMITTED || transaction.getStatus() == Status.STATUS_ACTIVE) {
                    for (IndexWriter writer : writers) {
                        writer.commit();
                        try {
                            writer.close();
                        } finally {
                            if (IndexWriter.isLocked(writer.getDirectory())) {
                                IndexWriter.unlock(writer.getDirectory());
                            }
                        }
                        transaction.putInContext("writers", null);
                    }
                    logger.debug("Indexed: {} Unindexed {}", indexed, unindexed);
                } else if (transaction.getStatus() == Status.STATUS_ROLLEDBACK) {
                    for (IndexWriter writer : writers) {
                        writer.rollback();
                        try {
                            writer.close();
                        } finally {
                            if (IndexWriter.isLocked(writer.getDirectory())) {
                                IndexWriter.unlock(writer.getDirectory());
                            }
                        }
                        transaction.putInContext("writers", null);
                    }
                    logger.debug("Aborted Indexation");
                }
            } catch (IOException | SystemException e) {
                logger.error("Error after commit indexes", e);
            } finally {
                for (IndexWriter writer : writers) {
                    try {
                        try {
                            writer.close();
                        } finally {
                            if (IndexWriter.isLocked(writer.getDirectory())) {
                                logger.error("I'm trying to unlock something that should be already unlocked");
                                IndexWriter.unlock(writer.getDirectory());
                            }
                        }
                    } catch (IOException e) {
                        logger.error("AfterCommit: Something went really REALLY bad, finally writer closed", e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("resource")
    private IndexWriter getWriterFor(Map<Class<? extends Indexable>, IndexWriter> writers, Class<? extends Indexable> type) {
        try {
            if (!writers.containsKey(type)) {
                Directory directory = DomainIndexer.getLuceneDomainDirectory(type, true);
                Analyzer analyzer = new StandardAnalyzer(DomainIndexer.VERSION);
                IndexWriterConfig config = new IndexWriterConfig(DomainIndexer.VERSION, analyzer);
                config.setWriteLockTimeout(10 * 1000);
                writers.put(type, new IndexWriter(directory, config));
            }
            return writers.get(type);
        } catch (IOException e) {
            throw new DomainIndexException(e);
        }
    }
}
