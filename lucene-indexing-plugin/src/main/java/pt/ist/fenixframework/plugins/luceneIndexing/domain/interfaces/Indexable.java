package pt.ist.fenixframework.plugins.luceneIndexing.domain.interfaces;

import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;

/**
 * This interface is used in objects that know how to generate a IndexDocument
 * that will be indexed by lucene. This objects are automatically indexed and
 * searchable by lucene queries.
 * 
 * @author Paulo Abrantes
 */
public interface Indexable {
    public static enum IndexMode {
        MANUAL, SYNC, ASYNC;
    }

    public IndexMode getIndexMode();

    public IndexDocument getDocumentToIndex();
}
