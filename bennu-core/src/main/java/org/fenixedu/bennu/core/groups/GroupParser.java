package org.fenixedu.bennu.core.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;

final class GroupParser {

    /**
     * Parses the given expression, returning its corresponding {@link Group}.
     *
     * @param expression
     *         The expression to parse
     * @return The group corresponding to the given expression
     * @throws DomainException
     *         If the given expression is invalid
     * @throws NullPointerException
     *         If the given expression is {@code null}.
     */
    public static Group parse(String expression) {
        return new GroupParser(expression.toCharArray()).toGroup();
    }

    /*
     * Internal implementation
     *
     * All the methods that implement the old ANTLR constructs are properly annotated, with the grammar fragment that
     * created them.
     *
     * The parser keeps a cursor (currentPos) to the expression string, which is used when looking for tokens.
     */

    // The expression to parse
    private final char[] chars;

    // The position that is currently being looked at
    private int currentPos = 0;

    private GroupParser(char[] chars) {
        this.chars = chars;
    }

    /*
     * parse: expression EOF
     */
    private Group toGroup() {
        Group group = expression();
        if (!eof()) {
            throw groupParsingException("<EOF>", new String(chars, currentPos, chars.length - currentPos));
        }
        return group;
    }

    /*
     * expression: minus
     */
    private Group expression() {
        return minus();
    }

    /*
     * minus: or ('-' or)*
     */
    private Group minus() {
        Group group = or();

        while (consumeIfMatches('-')) {
            group = group.minus(or());
        }

        return group;
    }

    /*
     * or: and ('|' and)*
     */
    private Group or() {
        Group group = and();

        while (consumeIfMatches('|')) {
            group = group.or(and());
        }

        return group;
    }

    /*
     * and: not ('&' not)*
     */
    private Group and() {
        Group group = not();

        while (consumeIfMatches('&')) {
            group = group.and(not());
        }

        return group;
    }

    /*
     * not: '!' atom | atom
     */
    private Group not() {
        if (consumeIfMatches('!')) {
            return atom().not();
        } else {
            return atom();
        }
    }

    /*
     * atom: '#' IDENTIFIER | '(' expression ')' | function
     *
     * An atom is either a reference to a dynamic group, an expression surrounded by parentheses, or a function.
     */
    private Group atom() {
        if (consumeIfMatches('#')) {
            return Group.dynamic(identifier());
        } else if (consumeIfMatches('(')) {
            Group group = expression();
            consume(')');
            return group;
        } else {
            return function();
        }
    }

    /*
     * function: IDENTIFIER ('(' argument (',' argument)* ')')?
     *
     * A function is an identifier, optionally followed by an arbitrary number of arguments.
     */
    private Group function() {
        String name = identifier();
        Map<String, List<String>> arguments = null;
        if (consumeIfMatches('(')) {
            arguments = new HashMap<>();
            argument(arguments);
            while (consumeIfMatches(',')) {
                argument(arguments);
            }
            consume(')');
        }
        return CustomGroupRegistry.parse(name, arguments);
    }

    /*
     * argument: (IDENTIFIER '=')? (value | '[' (value (',' value)*)? ']')
     */
    private void argument(Map<String, List<String>> arguments) {
        String name = "";
        if (hasArgumentName()) {
            name = identifier();
            consume('=');
        }
        if (consumeIfMatches('[')) {
            argumentList(name, arguments);
        } else {
            arguments.computeIfAbsent(name, k -> new ArrayList<>()).add(value());
        }
    }

    /*
     * argumentList: (value (',' value)*)?
     *
     * Assumes that the '[' character has already been consumed
     */
    private void argumentList(String name, Map<String, List<String>> arguments) {
        // Argument list may be empty, so we cannot simply try and get a value
        if (!consumeIfMatches(']')) {
            arguments.computeIfAbsent(name, k -> new ArrayList<>()).add(value());
            while (consumeIfMatches(',')) {
                arguments.computeIfAbsent(name, k -> new ArrayList<>()).add(value());
            }
            consume(']');
        }
    }

    /*
     * value: IDENTIFIER | STRING
     */
    private String value() {
        if (consumeIfMatches('\'')) {
            return string();
        } else {
            return identifier();
        }
    }

    /*
     * Terminal Operators
     */

    /*
     * IDENTIFIER: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')+
     *
     * Retrieves all characters in the stream, until a non-identifier character or EOF is reached.
     *
     * Throws an exception if EOF was reached before actually reading anything
     */
    private String identifier() {
        int cursor = currentPos;

        while (!eof(cursor)) {
            char c = chars[cursor];
            if (isIdentifierChar(c)) {
                cursor++;
            } else {
                break;
            }
        }

        if (currentPos == cursor) {
            throw groupParsingException("<IDENTIFIER>", eof() ? "<EOF>" : String.valueOf(chars[currentPos]));
        }

        String value = new String(chars, currentPos, cursor - currentPos);
        currentPos = cursor;
        return value;
    }

    /*
     * A string is an arbitrary sequence of characters surrounded by single quotes. To add a single quote to the string itself,
     * one may simply add the sequence "\'".
     *
     * Throws an exception if EOF was reached before seeing the end quote.
     *
     * Assumes that the '\'' character has already been consumed.
     */
    private String string() {
        StringBuilder builder = new StringBuilder();

        while (!eof()) {
            // Read a character
            char c = chars[currentPos];
            currentPos++;

            // If we found a '\', check if the following character is a single quote
            if (c == '\\' && !eof(currentPos) && chars[currentPos] == '\'') {
                builder.append('\'');
                currentPos++;
            } else if (c != '\'') {
                // Append any non-single-quote character to the result
                builder.append(c);
            } else {
                // Return upon finding the single quote
                return builder.toString();
            }
        }

        throw groupParsingException("'", "<EOF>");
    }

    /*
     * Returns whether the next tokens represent a valid argument name (i.e., an identifier followed by a '=')
     */
    private boolean hasArgumentName() {
        consumeWhitespace();
        int i = currentPos;

        // Look up an identifier
        while (!eof(i)) {
            if (isIdentifierChar(chars[i])) {
                i++;
            } else {
                break;
            }
        }

        // Clear whitespace in between
        while (!eof(i)) {
            if (isWhitespace(chars[i])) {
                i++;
            } else {
                break;
            }
        }

        // Check if the last character is '='
        return !eof(i) && chars[i] == '=';
    }

    /*
     * Helper methods
     */

    /*
     * Determines whether the given character is part of a valid identifier
     */
    private boolean isIdentifierChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    /*
     * Consumes the next non-whitespace token, if it matches the provided one.
     *
     * Returns whether there was a match. Returns false if EOF was reached.
     */
    private boolean consumeIfMatches(char token) {
        consumeWhitespace();
        if (eof()) {
            return false;
        }
        if (chars[currentPos] == token) {
            currentPos++;
            return true;
        } else {
            return false;
        }
    }

    /*
     * Consumes every whitespace character in the beginning of the stream
     */
    private void consumeWhitespace() {
        while (!eof()) {
            if (isWhitespace(chars[currentPos])) {
                currentPos++;
            } else {
                break;
            }
        }
    }

    /*
     * Consumes the next non-whitespace token, throwing an exception if it doesn't match the provided token, or if EOF has been
     * reached
     */
    private void consume(char token) {
        consumeWhitespace();
        if (eof()) {
            throw groupParsingException(String.valueOf(token), "<EOF>");
        }
        char c = chars[currentPos];
        if (c == token) {
            currentPos++;
        } else {
            throw groupParsingException(String.valueOf(token), String.valueOf(c));
        }
    }

    /*
     * Returns whether the end of file has been reached
     */
    private boolean eof() {
        return eof(currentPos);
    }

    /*
     * Returns whether the given position represents the end of file
     */
    private boolean eof(int pos) {
        return pos == chars.length;
    }

    private DomainException groupParsingException(String expected, String given) {
        StringBuilder message = new StringBuilder();
        message.append('\n').append(new String(chars)).append('\n');
        IntStream.range(0, currentPos).forEach(i -> message.append(' '));
        message.append('^').append("\nExpected: ").append(expected).append(", got ").append(given);
        return BennuCoreDomainException.groupParsingError(message.toString());
    }

}
