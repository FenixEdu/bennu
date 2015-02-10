package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

/**
 * {@link TokenParser} for the 'lazyFor' node.
 * 
 * This node allow lazy iteration over a {@link Stream}, without
 * invoking extraneous terminal operations. In practice, this node
 * is a wrapper for {@link Stream#forEach(java.util.function.Consumer)}.
 * 
 * Unlike the regular 'for' node, no extra variables are defined, only
 * the iteration variable, named in the token's usage.
 * 
 * Accepted arguments are instance of {@link Stream}, {@link Collection} or arrays.
 * 
 * Example:
 * 
 * <pre>
 *    {% lazyFor item in itemStream %}
 *      {{item.name}}
 *    {% endLazyFor %}
 * </pre>
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 * @since 3.3
 *
 */
public class LazyForTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token) throws ParserException {
        TokenStream stream = this.parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip the 'lazyFor' token
        stream.next();

        // get the iteration variable
        String iterationVariable = this.parser.getExpressionParser().parseNewVariableName();

        stream.expect(Token.Type.NAME, "in");

        // get the iterable variable
        Expression<?> iterable = this.parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        BodyNode body = this.parser.subparse((test) -> test.test(Token.Type.NAME, "endLazyFor"));

        // skip the 'endLazyFor' token
        stream.next();

        stream.expect(Token.Type.EXECUTE_END);

        return new ForNode(lineNumber, iterationVariable, iterable, body);
    }

    private static final class ForNode extends AbstractRenderableNode {

        private final String variableName;
        private final Expression<?> iterableExpression;
        private final BodyNode body;

        public ForNode(int lineNumber, String variableName, Expression<?> iterableExpression, BodyNode body) {
            super(lineNumber);
            this.variableName = variableName;
            this.iterableExpression = iterableExpression;
            this.body = body;
        }

        @Override
        public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
            Object value = iterableExpression.evaluate(self, context);
            if (value == null) {
                return;
            }

            Stream<?> stream = getStream(value);

            context.pushScope(new HashMap<String, Object>());
            stream.forEach((obj) -> {
                try {
                    context.put(variableName, obj);
                    body.render(self, writer, context);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            context.popScope();
        }

        private Stream<?> getStream(Object obj) {
            if (obj instanceof Stream) {
                return (Stream<?>) obj;
            } else if (obj instanceof Collection) {
                return ((Collection<?>) obj).stream();
            } else if (obj.getClass().isArray()) {
                return Arrays.stream((Object[]) obj);
            } else {
                throw new ClassCastException(obj + " cannot be cast to Stream");
            }
        }

        @Override
        public void accept(NodeVisitor visitor) {
            visitor.visit(this);
        }

    }

    @Override
    public String getTag() {
        return "lazyFor";
    }

}
