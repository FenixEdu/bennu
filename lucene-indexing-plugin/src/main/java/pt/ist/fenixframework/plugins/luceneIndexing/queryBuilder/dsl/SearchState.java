package pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl;

public class SearchState extends DSLState {

    SearchState(DSLState state) {
	this.buffer = state.buffer;
    }

    public SearchState(DSLState part, boolean subExpression) {
	this.buffer = new StringBuilder();
	if (subExpression) {
	    appendToBuffer("(");
	}
	appendToBuffer(part.toString());
	if (subExpression) {
	    appendToBuffer(")");
	}
    }

    public BuildingState and() {
	return and(buffer);
    }

    public BuildingState or() {
	return or(buffer);
    }

    public SearchModifiedState boost(int boostFactor) {
	appendToBuffer("^");
	appendToBuffer(String.valueOf(boostFactor));
	return new SearchModifiedState(buffer);
    }

    public SearchModifiedState fuzzy(double fuzzyFactor) {
	if (fuzzyFactor > 1) {
	    throw new IllegalArgumentException("fuzzyness should be between 0 and 1");
	}
	char lastChar = buffer.charAt(buffer.length() - 1);
	if (lastChar == '"' || lastChar == ')') {
	    throw new IllegalStateException("fuzzyness can only be applied to one single word");
	}
	appendToBuffer("~");
	appendToBuffer(String.valueOf(fuzzyFactor));
	return new SearchModifiedState(buffer);
    }
}
