/*
 * @(#)Search.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixWebFramework.services.Service;

public abstract class Search<T> implements Serializable {

    protected abstract class SearchResultSet<T> extends HashSet<T> {

	public SearchResultSet(Collection<? extends T> c) {
	    super(c);
	}

	@Override
	public boolean add(final T t) {
	    return matchesSearchCriteria(t) && super.add(t);
	}

	protected abstract boolean matchesSearchCriteria(final T t);

	protected boolean matchCriteria(final String criteria, final String value) {
	    return criteria == null || criteria.length() == 0 || criteria.equals(value);
	}

    }

    public abstract Set<T> search();

    public Set<T> getResult() {
	return search();
    }

    @Service
    public void persistSearch(String name) {
	persist(name);
    }

    protected void persist(String name) {
    }

}
