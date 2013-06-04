package pt.ist.bennu.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import pt.ist.bennu.search.DomainIndexer.DefaultIndexFields;

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

    private Class<? extends Indexable> indexableClass;

    public IndexDocument(Indexable indexable) {
        this.indexId = indexable.getExternalId();
        this.indexableClass = indexable.getClass();
        this.values = new HashMap<>();
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

    public Class<? extends Indexable> getIndexableClass() {
        return this.indexableClass;
    }

    public Document getLuceneDocument() {
        Document doc = new Document();
        StringBuilder builder = new StringBuilder();

        doc.add(new Field(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), getIndexId(), Field.Store.YES,
                Field.Index.NOT_ANALYZED));

        for (IndexableField indexableField : getIndexableFields()) {
            String value = getValueForField(indexableField);
            doc.add(new Field(indexableField.getFieldName(), value, Field.Store.NO, Field.Index.ANALYZED));
            builder.append(value);
            builder.append(" ");
        }
        doc.add(new Field(DefaultIndexFields.DEFAULT_FIELD.getFieldName(), builder.toString(), Field.Store.NO,
                Field.Index.ANALYZED));
        return doc;
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
