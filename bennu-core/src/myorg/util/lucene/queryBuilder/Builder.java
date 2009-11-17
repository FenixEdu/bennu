package myorg.util.lucene.queryBuilder;

import myorg.util.lucene.IndexableField;
import myorg.util.lucene.queryBuilder.dsl.BuildingState;
import myorg.util.lucene.queryBuilder.dsl.DSLState;
import myorg.util.lucene.queryBuilder.dsl.SearchState;

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
