package pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl;

import org.apache.lucene.queryParser.QueryParser;

import pt.ist.fenixframework.plugins.luceneIndexing.IndexableField;

public class BuildingState extends DSLState {

	private static final String EMPTY_SPACE = " ";

	BuildingState(StringBuilder buffer) {
		this.buffer = buffer;
	}

	public BuildingState() {
		this.buffer = new StringBuilder();
	}

	private void write(String keyword, IndexableField field, String value) {
		if (value == null) {
			throw new IllegalArgumentException("all expressions must contain a value");
		}

		if (keyword != null) {
			appendToBuffer(EMPTY_SPACE);
			appendToBuffer(keyword);
			appendToBuffer(EMPTY_SPACE);
		}
		if (field != null) {
			appendToBuffer(field.getFieldName());
			appendToBuffer(":");
		}
		int indexOfSpace = value.indexOf(EMPTY_SPACE);
		if (indexOfSpace > 0) {
			appendToBuffer("\"");
		}
		appendToBuffer(value);
		if (indexOfSpace > 0) {
			appendToBuffer("\"");
		}
	}

	public SearchState not(String term) {
		write("NOT", null, QueryParser.escape(term));
		return new SearchState(this);
	}

	public SearchState not(IndexableField field, String term) {
		write("NOT", field, QueryParser.escape(term));
		return new SearchState(this);
	}

	public SearchState wildCardMatch(String wildCardedValue) {
		write(null, null, wildCardedValue);
		return new SearchState(this);
	}

	public SearchState wildCardMatch(IndexableField field, String wildCardedValue) {
		write(null, field, wildCardedValue);
		return new SearchState(this);
	}

	public SearchState matches(String value) {
		write(null, null, QueryParser.escape(value));
		return new SearchState(this);
	}

	public SearchState matches(IndexableField field, String value) {
		write(null, field, QueryParser.escape(value));
		return new SearchState(this);
	}

	public SearchState expression(SearchState queryPart) {
		appendToBuffer("(");
		appendToBuffer(queryPart.toString());
		appendToBuffer(")");
		return new SearchState(this);
	}

	@Override
	public String finish() {
		throw new IllegalStateException("Cannot finish the query in an AND or OR keywords");
	}

	@Override
	public String toString() {
		return this.buffer.toString();
	}

}
