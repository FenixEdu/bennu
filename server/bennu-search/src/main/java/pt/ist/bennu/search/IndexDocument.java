package pt.ist.bennu.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormatter;

import pt.ist.bennu.search.DomainIndexer.DefaultIndexFields;

import com.google.common.base.Joiner;

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
    private final String id;

    private final Document document = new Document();

    private final List<String> accumulated = new ArrayList<>();

    public IndexDocument(Indexable indexable) {
        id = indexable.getExternalId();
        document.add(new StringField(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), id, Field.Store.YES));
    }

    public IndexDocument indexString(IndexableField field, String value, Store store) {
        document.add(new StringField(field.getFieldName(), DomainIndexer.normalize(value), store));
        accumulate(value);
        return this;
    }

    public IndexDocument indexString(IndexableField field, String value) {
        return indexString(field, value, Store.NO);
    }

    public IndexDocument indexText(IndexableField field, String value, Store store) {
        document.add(new TextField(field.getFieldName(), DomainIndexer.normalize(value), store));
        accumulate(value);
        return this;
    }

    public IndexDocument indexText(IndexableField field, String value) {
        return indexText(field, value, Store.NO);
    }

    public IndexDocument indexDate(IndexableField field, Partial date, DateTimeFormatter format) {
        return indexString(field, date.toString(format));
    }

    public IndexDocument indexNumber(IndexableField field, long value) {
        document.add(new LongField(field.getFieldName(), value, Store.NO));
        return this;
    }

    public Document getLuceneDocument() {
        document.add(new TextField(DefaultIndexFields.DEFAULT_FIELD.getFieldName(), Joiner.on(' ').join(accumulated),
                Field.Store.NO));
        return document;
    }

    private void accumulate(String value) {
        accumulated.add(DomainIndexer.normalize(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexDocument) {
            return id.equals(((IndexDocument) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
