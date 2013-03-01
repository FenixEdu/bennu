/*
 * CompositionGroup.java
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
package pt.ist.bennu.core.domain.groups;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * N-ary group composition.
 * 
 * @see BennuGroup
 */
public abstract class CompositionGroup extends CompositionGroup_Base {
    protected CompositionGroup() {
    }

    protected void init(Set<BennuGroup> children) {
        getChildrenSet().addAll(children);
    }

    private class PresentationNameTransformer implements Function<BennuGroup, String> {
        @Override
        public String apply(BennuGroup input) {
            return input.getPresentationName();
        }
    }

    @Override
    public String getPresentationName() {
        return "("
                + Joiner.on(" " + operator() + " ")
                        .join(Iterables.transform(getChildrenSet(), new PresentationNameTransformer())) + ")";
    }

    private class ExpressionTransformer implements Function<BennuGroup, String> {
        @Override
        public String apply(BennuGroup input) {
            return input.expression();
        }
    }

    @Override
    public String expression() {
        return "(" + Joiner.on(" " + operator() + " ").join(Iterables.transform(getChildrenSet(), new ExpressionTransformer()))
                + ")";
    }

    protected abstract String operator();
}
