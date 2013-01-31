package pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl;

public class SearchModifiedState extends DSLState {

	SearchModifiedState(StringBuilder buffer) {
		this.buffer = buffer;
	}

	public BuildingState and() {
		return and(buffer);
	}

	public BuildingState or() {
		return or(buffer);
	}
}
