package pt.ist.fenixframework.plugins.luceneIndexing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jvstm.TransactionalCommand;

import org.apache.log4j.Logger;

import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexingRequest;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.LuceneSearchPluginRoot;
import pt.ist.fenixframework.pstm.Transaction;

public class IndexingManager implements Runnable {

    protected static final long DEFAULT_SLEEP_TIME = 60000;
    protected static final Logger LOGGER = Logger.getLogger(IndexingManager.class.getName());
    protected static final int DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT = 10000;

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
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    private int indexFiles() {
	Transaction.withTransaction(new TransactionalCommand() {

	    @Override
	    public void doIt() {
		final Set<IndexDocument> documentsToIndex = new HashSet<IndexDocument>();
		List<IndexingRequest> indexingRequests = LuceneSearchPluginRoot.getInstance().getIndexingRequests();
		int size = indexingRequests.size();

		List<IndexingRequest> subList = indexingRequests.subList(0, Math
			.min(DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT, size));
		Iterator<IndexingRequest> requestIterator = subList.iterator();

		while (requestIterator.hasNext()) {
		    IndexingRequest request = requestIterator.next();
		    documentsToIndex.add(request.getIndex());
		    requestIterator.remove();
		    request.delete();
		}

		if (!documentsToIndex.isEmpty()) {
		    LOGGER.info("Indexing " + documentsToIndex.size() + " documents out of " + size);
		    long t1 = System.currentTimeMillis();
		    DomainIndexer.getInstance().indexDomainObjects(documentsToIndex);
		    long t2 = System.currentTimeMillis();
		    LOGGER.info("Finished indexation. Took: " + (t2 - t1) + "ms.");
		}

		yetToIndex = size - DEFAULT_MAX_REQUEST_PROCESSES_PER_BEAT;
	    }

	});

	return yetToIndex;
    }
}
