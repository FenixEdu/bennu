package myorg.domain.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pt.ist.fenixframework.DomainObject;

public class IndexDocument {

    private Map<String, String> values;
    private String indexId;
    private Class<? extends DomainObject> domainObjectClass;

    public IndexDocument(DomainObject domainObject) {
	this.indexId = domainObject.getExternalId();
	this.domainObjectClass = domainObject.getClass();
	this.values = new HashMap<String, String>();
    }

    public void indexField(String name, String value) {
	values.put(name, value);
    }

    public Set<String> getIndexableFields() {
	return values.keySet();
    }

    public String getValueForField(String field) {
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
