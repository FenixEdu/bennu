/*
 * @(#)LuceneBasedAutoCompleteProvider.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Lucene Integration Module.
 *
 *   The Bennu Lucene Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Bennu Lucene Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Bennu Lucene Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.lucene.presentationTier.renderers.autoCompleteProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.QueryParser;

import pt.ist.bennu.core.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DefaultIndexFields;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexableField;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

/**
 * 
 * @author Pedro Santos
 * 
 */
public abstract class LuceneBasedAutoCompleteProvider implements AutoCompleteProvider {

	@Override
	public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
		Class<? extends DomainObject> domainObjectClass = getSearchClass(argsMap);
		List<? extends DomainObject> search =
				DomainIndexer.getInstance().search(domainObjectClass, getSearchField(), valueProcessingStrategy(value), maxCount);
		return search;
	}

	protected IndexableField getSearchField() {
		return DefaultIndexFields.DEFAULT_FIELD;
	}

	protected abstract Class<? extends DomainObject> getSearchClass(Map<String, String> argsMap);

	protected String valueProcessingStrategy(String value) {
		return StringNormalizer.normalize(QueryParser.escape(value.trim().replaceAll("\\s\\s+", " "))).toLowerCase()
				.replaceAll(" ", " AND ")
				+ "*";
	}

}
