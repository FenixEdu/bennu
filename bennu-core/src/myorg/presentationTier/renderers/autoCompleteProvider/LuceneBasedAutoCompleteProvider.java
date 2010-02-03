package myorg.presentationTier.renderers.autoCompleteProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexableField;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DefaultIndexFields;
import pt.ist.fenixframework.plugins.luceneIndexing.util.LuceneStringEscaper;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public abstract class LuceneBasedAutoCompleteProvider implements AutoCompleteProvider {

    @Override
    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	Class<? extends DomainObject> domainObjectClass = getSearchClass(argsMap);
	List<? extends DomainObject> search = DomainIndexer.getInstance().search(domainObjectClass, getSearchField(),
		valueProcessingStrategy(value), maxCount);
	return search;
    }

    protected IndexableField getSearchField() {
	return DefaultIndexFields.DEFAULT_FIELD;
    }

    protected abstract Class<? extends DomainObject> getSearchClass(Map<String, String> argsMap);

    protected String valueProcessingStrategy(String value) {
	return StringNormalizer.normalize(LuceneStringEscaper.escape(value.trim().replaceAll("\\s\\s+", " "))).toLowerCase()
		.replaceAll(" ", " AND ")
		+ "*";
    }

}
