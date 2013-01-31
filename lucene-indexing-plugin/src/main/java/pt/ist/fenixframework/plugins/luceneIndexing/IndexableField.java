package pt.ist.fenixframework.plugins.luceneIndexing;

/**
 * This interface is used in objects that represent index names. Along with this
 * plugin there's already an implementation of IndexableField, the enum
 * pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DefaultIndexFields
 * which offers, among others, the DEFAULT_FIELD (which is the one recommended
 * to do generic searches).
 * 
 * You should implement this interface when there's the need to search for
 * specific indexes of a given domain object.
 * 
 * @author Paulo Abrantes
 */
public interface IndexableField {

	public String getFieldName();
}
