/*
 * GroupExpressionParser.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.grouplanguage;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.fenixedu.bennu.core.domain.groups.Group;

public class GroupExpressionParser {
    public static Group parse(String expression) throws RecognitionException, IOException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(expression.getBytes())) {
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
