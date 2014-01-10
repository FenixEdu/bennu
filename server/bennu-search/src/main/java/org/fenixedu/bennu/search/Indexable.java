package org.fenixedu.bennu.search;

import pt.ist.fenixframework.DomainObject;

/**
 * This interface is used in objects that know how to generate a IndexDocument
 * that will be indexed by lucene. This objects are automatically indexed and
 * searchable by lucene queries.
 * 
 * @author Paulo Abrantes
 */
public interface Indexable extends DomainObject {
    public IndexDocument getDocumentToIndex();
}
