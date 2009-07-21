package myorg.util.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import myorg.domain.index.IndexDirectory;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

public class DomainIndexer {

    public static final String DEFAULT_FIELD = "all";

    public static final int DEFAULT_MAX_SIZE = 200;

    public static class DomainIndexException extends RuntimeException {

	public DomainIndexException(Exception e) {
	    super(e);
	}
    }

    private static DomainIndexer singletonInstance;

    private DomainIndexer() {

    }

    private static synchronized void init() {
	if (singletonInstance == null) {
	    singletonInstance = new DomainIndexer();
	}
    }

    public static DomainIndexer getInstance() {
	if (singletonInstance == null) {
	    init();
	}
	return singletonInstance;
    }

    private LuceneDomainDirectory getIndexDirectory(String name) {
	IndexDirectory directory = IndexDirectory.getIndexDirectory(name);
	LuceneDomainDirectory domainDirectory = null;
	if (directory == null) {
	    domainDirectory = initIndex(name);
	} else {
	    domainDirectory = new LuceneDomainDirectory(directory);
	}
	return domainDirectory;
    }

    @Service
    private LuceneDomainDirectory initIndex(String name) {
	IndexDirectory directory = IndexDirectory.createNewIndexDirectory(name);
	LuceneDomainDirectory domainDirectory = new LuceneDomainDirectory(directory);
	try {
	    IndexWriter writer = new IndexWriter(domainDirectory, new StandardAnalyzer(), true, MaxFieldLength.UNLIMITED);
	    writer.close();
	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (LockObtainFailedException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	}
	return domainDirectory;
    }

    public void indexDomainObject(DomainObject domainObject, Map<String, String> indexMap) {
	String name = findDirectoryNameForClass(domainObject.getClass());
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(name);

	    if (isIndexed(domainObject, luceneDomainDirectory)) {
		unindexDomainObject(domainObject);
	    }
	    IndexWriter writer = new IndexWriter(luceneDomainDirectory, new StandardAnalyzer(), false, MaxFieldLength.UNLIMITED);
	    writer.setMergeScheduler(new SerialMergeScheduler());
	    writer.addDocument(createDocument(domainObject, indexMap));

	    writer.optimize();
	    writer.close();

	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	}

    }

    private boolean isIndexed(DomainObject domainObject, LuceneDomainDirectory luceneDomainDirectory)
	    throws CorruptIndexException, IOException, ParseException {
	Searcher searcher = new IndexSearcher(luceneDomainDirectory);
	QueryParser queryParser = new QueryParser("OID", new StandardAnalyzer());
	Query query = queryParser.parse(String.valueOf(domainObject.getOID()));

	TopDocs topDocs = searcher.search(query, 10);
	int length = topDocs.scoreDocs.length;
	if (length > 1) {
	    System.out.println("[WARN] Various hits for OID: " + domainObject.getOID() + " hits:" + length);
	}
	return length > 0;
    }

    public boolean isIndexed(DomainObject domainObject) {
	String name = findDirectoryNameForClass(domainObject.getClass());
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(name);
	    return isIndexed(domainObject, luceneDomainDirectory);
	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	}

    }

    private void unindexDomainObject(DomainObject domainObject, LuceneDomainDirectory luceneDomainDirectory)
	    throws CorruptIndexException, IOException, ParseException {
	QueryParser queryParser = new QueryParser("OID", new StandardAnalyzer());
	Query query = queryParser.parse(String.valueOf(domainObject.getOID()));

	IndexWriter writer = new IndexWriter(luceneDomainDirectory, new StandardAnalyzer(), false, MaxFieldLength.UNLIMITED);
	writer.setMergeScheduler(new SerialMergeScheduler());
	writer.deleteDocuments(query);
	writer.optimize();
	writer.close();
    }

    public void unindexDomainObject(DomainObject domainObject) {
	String name = findDirectoryNameForClass(domainObject.getClass());
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(name);
	    unindexDomainObject(domainObject, luceneDomainDirectory);
	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	}

    }

    public <T extends DomainObject> List<T> search(Class<T> domainObjectClass, String value) {
	return search(domainObjectClass, DEFAULT_FIELD, value, DEFAULT_MAX_SIZE);
    }

    public <T extends DomainObject> List<T> search(Class<T> domainObjectClass, String slot, String value, int maxHits) {

	List<T> domainObjects = new ArrayList<T>();
	if (StringUtils.isEmpty(value)) {
	    return domainObjects;
	}
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(findDirectoryNameForClass(domainObjectClass));

	    Searcher searcher = new IndexSearcher(luceneDomainDirectory);
	    QueryParser queryParser = new QueryParser(slot, new StandardAnalyzer());
	    Query query = queryParser.parse(value);

	    TopDocs topDocs = searcher.search(query, maxHits);

	    for (int i = 0; i < topDocs.scoreDocs.length; i++) {
		int docId = topDocs.scoreDocs[i].doc;
		Document doc = searcher.doc(docId);

		Long OID = Long.valueOf(doc.get(("OID")));
		DomainObject domainObject = Transaction.getObjectForOID(OID.longValue());
		domainObjects.add((T) domainObject);
	    }

	    searcher.close();
	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	}
	return domainObjects;
    }

    private String findDirectoryNameForClass(Class<? extends DomainObject> domainObjectClass) {
	Class<? extends DomainObject> superclass = (Class<? extends DomainObject>) domainObjectClass.getSuperclass();
	if (superclass.getName().endsWith("_Base")) {
	    superclass = (Class<? extends DomainObject>) superclass.getSuperclass();
	}

	return superclass == AbstractDomainObject.class ? domainObjectClass.getName() : findDirectoryNameForClass(superclass);
    }

    private Document createDocument(DomainObject domainObject, Map<String, String> indexMap) {
	Document doc = new Document();
	StringBuilder builder = new StringBuilder();

	doc.add(new Field("OID", String.valueOf(domainObject.getOID()), Field.Store.COMPRESS, Field.Index.NOT_ANALYZED));

	for (String key : indexMap.keySet()) {
	    String value = indexMap.get(key);
	    doc.add(new Field(key, value, Field.Store.NO, Field.Index.ANALYZED));
	    builder.append(value);
	    builder.append(" ");
	}

	doc.add(new Field(DEFAULT_FIELD, builder.toString(), Field.Store.NO, Field.Index.ANALYZED));
	return doc;
    }
}
