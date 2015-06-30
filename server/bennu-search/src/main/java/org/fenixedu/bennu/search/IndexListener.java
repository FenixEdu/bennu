package org.fenixedu.bennu.search;

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
import org.fenixedu.bennu.search.IndexDocument.DefaultIndexFields;
import org.fenixedu.bennu.search.domain.DomainIndexSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            int indexed = 0;
            int unindexed = 0;
            Map<Class<? extends Indexable>, IndexWriter> writers = new HashMap<>();
            Set<Directory> directories = new HashSet<>();
            try {
                for (DomainObject domainObject : new HashSet<>(introspector.getNewObjects())) {
                    if (domainObject instanceof Searchable) {
                        for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                            IndexDocument document = indexableObject.getDocumentToIndex();
                            getWriterFor(directories, writers, indexableObject.getClass()).addDocument(
                                    document.getLuceneDocument());
                            indexed++;
                        }
                    }
                }
                for (DomainObject domainObject : introspector.getModifiedObjects()) {
                    if (domainObject instanceof Searchable) {
                        if (!introspector.isDeleted(domainObject)) {
                            for (Indexable indexableObject : ((Searchable) domainObject).getObjectsToIndex()) {
                                IndexDocument document = indexableObject.getDocumentToIndex();
                                IndexWriter writer = getWriterFor(directories, writers, indexableObject.getClass());
                                writer.deleteDocuments(new Term(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), domainObject
                                        .getExternalId()));
                                writer.addDocument(document.getLuceneDocument());
                                indexed++;
                            }
                        } else {
                            @SuppressWarnings("unchecked")
                            IndexWriter writer =
                                    getWriterFor(directories, writers, (Class<? extends Indexable>) domainObject.getClass());
                            writer.deleteDocuments(new Term(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), domainObject
                                    .getExternalId()));
                            unindexed++;
                        }
                    }
                }
                Set<Indexable> reindex = FenixFramework.getTransaction().getFromContext(DomainIndexSystem.FORCE_REINDEX_LIST);
                if (reindex != null) {
                    for (Indexable indexableObject : reindex) {
                        IndexDocument document = indexableObject.getDocumentToIndex();
                        IndexWriter writer = getWriterFor(directories, writers, indexableObject.getClass());
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
                }
            } catch (IOException e) {
                logger.error("Before commit: something crashed", e);
            } finally {
                transaction.putInContext("directories", directories);
                transaction.putInContext("writers", writers.values());
                transaction.putInContext("indexed", indexed);
                transaction.putInContext("unindexed", unindexed);
            }
        }
    }

    @Override
    public void afterCommit(Transaction transaction) {
        try {
            Collection<IndexWriter> writers = transaction.getFromContext("writers");
            if (writers != null && !writers.isEmpty()) {
                try {
                    if (transaction.getStatus() == Status.STATUS_COMMITTED || transaction.getStatus() == Status.STATUS_ACTIVE) {
                        Integer indexed = transaction.getFromContext("indexed");
                        Integer unindexed = transaction.getFromContext("unindexed");
                        logger.debug("Indexed: {} Unindexed {}", indexed, unindexed);

                        for (IndexWriter writer : writers) {
                            writer.commit();
                        }
                    } else if (transaction.getStatus() == Status.STATUS_ROLLEDBACK) {
                        for (IndexWriter writer : writers) {
                            writer.rollback();
                        }
                        logger.debug("Aborted Indexation");
                    }
                } catch (IOException | SystemException e) {
                    logger.error("Error after commit indexes", e);
                } finally {
                    for (IndexWriter writer : writers) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            logger.error("AfterCommit: Something went really REALLY bad, finally writer closed", e);
                        }
                    }
                }
            }
            Set<Directory> directories = transaction.getFromContext("directories");
            if (directories != null && !directories.isEmpty()) {
                for (final Directory directory : directories) {
                    try {
                        if (IndexWriter.isLocked(directory)) {
                            logger.error("I'm trying to unlock something that should be already unlocked");
                            IndexWriter.unlock(directory);
                        }
                    } catch (IOException e) {
                        logger.error("AfterCommit: Check and unlock directory", e);
                    }
                    try {
                        directory.close();
                    } catch (IOException e) {
                        logger.error("AfterCommit: Unable to close directory", e);
                    }
                }
            }
        } finally {
            transaction.putInContext("directories", null);
            transaction.putInContext("writers", null);
            transaction.putInContext("indexed", null);
            transaction.putInContext("unindexed", null);
        }
    }

    @SuppressWarnings("resource")
    private IndexWriter getWriterFor(Set<Directory> directories, Map<Class<? extends Indexable>, IndexWriter> writers,
            Class<? extends Indexable> type) {
        try {
            if (!writers.containsKey(type)) {
                Directory directory = DomainIndexSystem.getLuceneDomainDirectory(type, true);
                directories.add(directory);
                Analyzer analyzer = new StandardAnalyzer(DomainIndexSystem.VERSION);
                IndexWriterConfig config = new IndexWriterConfig(DomainIndexSystem.VERSION, analyzer);
                config.setWriteLockTimeout(10 * 1000);
                writers.put(type, new IndexWriter(directory, config));
            }
        } catch (IOException e) {
            logger.error("Error after getWriterFor", e);
        }
        return writers.get(type);
    }
}
