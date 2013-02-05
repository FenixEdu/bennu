/*
 * GroupExpressionParser.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.grouplanguage;

import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.tools.ant.filters.StringInputStream;

import pt.ist.bennu.core.domain.groups.GroupException;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

public class GroupExpressionParser {
    public static PersistentGroup parse(String expression) throws RecognitionException, IOException, GroupException {
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
