package pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder;

import pt.ist.fenixframework.plugins.luceneIndexing.IndexableField;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.BuildingState;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.DSLState;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.SearchState;

/**
 * The Builder is the starting point of a simple fluent interface DSL to
 * generate advanced queries for lucene. The best way to use it, is to do static
 * imports of all the methods below and then through 'chainability' create the
 * query.
 * 
 * @author Paulo Abrantes
 * 
 */
public class Builder {

	public static SearchState wildCardMatch(IndexableField field, String wildCardedValue) {
		BuildingState queryPart = new BuildingState();
		return queryPart.wildCardMatch(field, wildCardedValue);
	}

	public static SearchState wildCardMatch(String wildCardedValue) {
		BuildingState queryPart = new BuildingState();
		return queryPart.wildCardMatch(wildCardedValue);
	}

	public static SearchState expression(DSLState part) {
		return new SearchState(part, true);
	}

	public static SearchState matches(String value) {
		BuildingState queryPart = new BuildingState();
		return queryPart.matches(value);
	}

	public static SearchState matches(IndexableField field, String value) {
		BuildingState queryPart = new BuildingState();
		return queryPart.matches(field, value);
	}

}
