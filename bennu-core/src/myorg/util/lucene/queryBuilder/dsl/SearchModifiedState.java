package myorg.util.lucene.queryBuilder.dsl;


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
