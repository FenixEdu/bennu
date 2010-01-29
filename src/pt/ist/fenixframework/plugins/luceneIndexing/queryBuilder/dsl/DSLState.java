package pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl;

import pt.ist.fenixframework.plugins.luceneIndexing.util.LuceneStringEscaper;

public class DSLState {

    protected StringBuilder buffer;

    protected static String escape(String valueToEscape) {
	return LuceneStringEscaper.escape(valueToEscape);
    }

    public boolean isEmpty() {
	return this.buffer.length() == 0;
    }

    public String finish() {
	return this.buffer.toString();
    }

    @Override
    public String toString() {
	return finish();
    }

    protected void appendToBuffer(String toAppend) {
	this.buffer.append(toAppend);
    }

    protected BuildingState and(StringBuilder buffer) {
	appendToBuffer(" AND ");
	return new BuildingState(buffer);
    }

    protected BuildingState or(StringBuilder buffer) {
	appendToBuffer(" OR ");
	return new BuildingState(buffer);
    }

    protected StringBuilder getBuffer() {
	return this.buffer;
    }
}
