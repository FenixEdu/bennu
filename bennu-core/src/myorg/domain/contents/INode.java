/*
 * @(#)INode.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain.contents;

import java.util.Comparator;
import java.util.Set;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public interface INode {

    public static Comparator<INode> COMPARATOR_BY_ORDER = new Comparator<INode>() {

	@Override
	public int compare(final INode node1, final INode node2) {
	    final int co = node1.getOrder().compareTo(node2.getOrder());
	    return co == 0 ? node2.hashCode() - node1.hashCode() : co;
	}

    };

    public Integer getOrder();

    public INode getParent();

    public Set<INode> getChildren();

    public Set<INode> getOrderedChildren();

    public Object getElement();

    public String asString();

    public MultiLanguageString getLink();

    public boolean isAccessible();

    public boolean isManager();

    public String getUrl();

    public boolean hasFunctionality();

}
