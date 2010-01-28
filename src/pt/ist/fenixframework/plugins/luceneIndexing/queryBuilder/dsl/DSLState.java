package pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl;

public class DSLState {

    protected StringBuilder buffer;

    // the \\ needs to be the 1st character since it's also the escape
    // character.
    private static String[] reservedWords = { "\\", "+", "-", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":",
	    "&&", "||" };

    private static String ESCAPE_STRING = "\\";

    protected static String escape(String valueToEscape) {
	StringBuilder builder = new StringBuilder(valueToEscape);
	for (String reservedWord : reservedWords) {
	    escape(builder, reservedWord);
	}
	return builder.toString();
    }

    private static void escape(StringBuilder buffer, String reservedWord) {
	int pos = 0;
	int index = -1;
	while ((index = buffer.indexOf(reservedWord, pos)) > 0) {
	    pos = index + 2;
	    buffer.insert(index, ESCAPE_STRING);
	}
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
