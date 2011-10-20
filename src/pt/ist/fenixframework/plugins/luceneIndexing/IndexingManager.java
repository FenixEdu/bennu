package pt.ist.fenixframework.plugins.luceneIndexing;

import java.util.HashMap;
import java.util.Map;

import jvstm.TransactionalCommand;

import org.apache.log4j.Logger;

import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexingRequest;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.LuceneSearchPluginRoot;
import pt.ist.fenixframework.pstm.Transaction;

public class IndexingManager implements Runnable {

    protected static final long DEFAULT_SLEEP_TIME = 1000;
    protected static final Logger LOGGER = Logger.getLogger(IndexingManager.class.getName());
    protected static final int DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT = 200;

    private int yetToIndex;

    @Override
    public void run() {
	while (true) {
	    try {
		Thread.sleep(DEFAULT_SLEEP_TIME);
		LOGGER.debug("Tick!");
		while (indexFiles() > 0) {
		    // keep doing it!
		}
	    } catch (Throwable t) {
		t.printStackTrace();
	    }
	}
    }

    private int indexFiles() {
	Transaction.withTransaction(new TransactionalCommand() {

	    @Override
	    public void doIt() {
		final Map<String, IndexDocument> requestMap = new HashMap<String, IndexDocument>();

		final LuceneSearchPluginRoot root = LuceneSearchPluginRoot.getInstance();
		for (final IndexingRequest request : root.getIndexingRequestsSet()) {
		    if (requestMap.size() > DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT) {
			break;
		    }
		    final String indexableExternalId = request.getIndexableExternalId();
		    if (!requestMap.containsKey(indexableExternalId)) {
			requestMap.put(indexableExternalId, request.getIndex());
		    }
		    request.delete();
		}

		final int remainder = root.getIndexingRequestsSet().size();
		if (!requestMap.isEmpty()) {
		    final int indexed = requestMap.size();
		    LOGGER.info("Indexing " + indexed + " documents out of " + (remainder + indexed));
		    long t1 = System.currentTimeMillis();
		    DomainIndexer.getInstance().indexDomainObjects(requestMap.values());
		    long t2 = System.currentTimeMillis();
		    LOGGER.info("Finished indexation. Took: " + (t2 - t1) + "ms.");
		}

		yetToIndex = remainder;
	    }

	});

	return yetToIndex;
    }
}
