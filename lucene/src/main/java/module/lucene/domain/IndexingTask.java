/*
 * @(#)IndexingTask.java
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
package module.lucene.domain;

import java.util.HashMap;
import java.util.Map;

import pt.ist.fenixframework.plugins.LuceneSearchPlugin;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexingRequest;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.LuceneSearchPluginRoot;

/**
 * 
 * @author Pedro Santos
 * 
 */
public class IndexingTask extends IndexingTask_Base {
	protected static final int DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT = 20000;

	public IndexingTask() {
		super();
	}

	@Override
	public void executeTask() {
		final Map<String, IndexDocument> requestMap = new HashMap<String, IndexDocument>();

		final LuceneSearchPluginRoot root = LuceneSearchPluginRoot.getInstance();
		for (final IndexingRequest request : root.getIndexingRequestsSet()) {
			final String indexableExternalId = request.getIndexableExternalId();
			if (requestMap.containsKey(indexableExternalId)) {
				request.delete();
			} else {
				if (requestMap.size() < DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT) {
					requestMap.put(indexableExternalId, request.getIndex());
					request.delete();
				} else {
					break;
				}
			}
		}

		final int remainder = root.getIndexingRequestsSet().size();
		if (!requestMap.isEmpty()) {
			final int indexed = requestMap.size();
			LuceneSearchPlugin.LOGGER.info("Indexing " + indexed + " documents out of " + (remainder + indexed));
			long t1 = System.currentTimeMillis();
			DomainIndexer.getInstance().indexDomainObjects(requestMap.values());
			long t2 = System.currentTimeMillis();
			LuceneSearchPlugin.LOGGER.info("Finished indexation. Took: " + (t2 - t1) + "ms.");
		}
	}

	@Override
	public String getLocalizedName() {
		return getClass().getName();
	}
}
