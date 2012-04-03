package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexableField;

/**
 * This represents a set of lucene indexes for a given domain object. Adding
 * indexFields to this object can be done using the ones already given by
 * pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DefaultIndexFields
 * Although implementations of IndexableField can be created to give more
 * expressiveness to the indexes.
 * 
 * @author Paulo Abrantes
 */
public class IndexDocument {

    private Map<IndexableField, String> values;
    private String indexId;
    private Class<? extends DomainObject> domainObjectClass;

    public IndexDocument(DomainObject domainObject) {
	this.indexId = domainObject.getExternalId();
	this.domainObjectClass = domainObject.getClass();
	this.values = new HashMap<IndexableField, String>();
    }

    public void indexField(IndexableField field, String value) {
	values.put(field, value);
    }

    public Set<IndexableField> getIndexableFields() {
	return values.keySet();
    }

    public String getValueForField(IndexableField field) {
	return values.get(field);
    }

    public String getIndexId() {
	return this.indexId;
    }

    public Class<? extends DomainObject> getDomainObjectClass() {
	return this.domainObjectClass;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof IndexDocument) {
	    IndexDocument otherDocument = (IndexDocument) obj;
	    return indexId.equals(otherDocument.getIndexId());
	}

	return false;
    }

    @Override
    public int hashCode() {
	return this.indexId.hashCode();
    }
}
