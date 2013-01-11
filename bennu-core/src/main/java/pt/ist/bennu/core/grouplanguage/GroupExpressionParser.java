package pt.ist.bennu.core.grouplanguage;

import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.tools.ant.filters.StringInputStream;

import pt.ist.bennu.core.domain.groups.PersistentGroup;

public class GroupExpressionParser {
	public static PersistentGroup parse(String expression) throws RecognitionException, IOException {
		try (StringInputStream stream = new StringInputStream(expression)) {
			ANTLRInputStream input = new ANTLRInputStream(stream);
			GroupLexer lexer = new GroupLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			GroupParser parser = new GroupParser(tokens);
			CommonTree tree = (CommonTree) parser.definition().getTree();

			CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
			GroupTree evaluator = new GroupTree(nodes);
			return evaluator.definition().value.group();
		}
	}
}
