/*
 * @(#)LuceneBasedAutoCompleteProvider.java
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

package myorg.presentationTier.renderers.autoCompleteProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexableField;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DefaultIndexFields;
import pt.ist.fenixframework.plugins.luceneIndexing.util.LuceneStringEscaper;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public abstract class LuceneBasedAutoCompleteProvider implements AutoCompleteProvider {

    @Override
    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	Class<? extends DomainObject> domainObjectClass = getSearchClass(argsMap);
	List<? extends DomainObject> search = DomainIndexer.getInstance().search(domainObjectClass, getSearchField(),
		valueProcessingStrategy(value), maxCount);
	return search;
    }

    protected IndexableField getSearchField() {
	return DefaultIndexFields.DEFAULT_FIELD;
    }

    protected abstract Class<? extends DomainObject> getSearchClass(Map<String, String> argsMap);

    protected String valueProcessingStrategy(String value) {
	return StringNormalizer.normalize(LuceneStringEscaper.escape(value.trim().replaceAll("\\s\\s+", " "))).toLowerCase()
		.replaceAll(" ", " AND ")
		+ "*";
    }

}
