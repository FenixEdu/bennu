package pt.ist.fenixframework.plugins.luceneIndexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexDirectory;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.DSLState;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.OneBoxDomainObject;

/**
 * DomainIndexer is the core of this plugin. This is a singleton class that
 * contains the methods to index, delete and search indexes for domain objects.
 * 
 * @author Paulo Abrantes
 */
public class DomainIndexer {

    public static enum DefaultIndexFields implements IndexableField {
	DEFAULT_FIELD("all"), IDENTIFIER_FIELD("OID"), GENERIC_FIELD("generic");

	private String fieldName;

	private DefaultIndexFields(String fieldName) {
	    this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
	    return fieldName;
	}

    }

    public static final int DEFAULT_MAX_SIZE = 200;

    public static class DomainIndexException extends RuntimeException {

	public DomainIndexException(Exception e) {
	    super(e);
	}
    }

    private int maxClouseCount = 10000;
    
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

    
    public int getMaxClouseCount() {
        return maxClouseCount;
    }

    public void setMaxClouseCount(int maxClouseCount) {
        this.maxClouseCount = maxClouseCount;
    }

    private boolean hasIndexDirectory(String name) {
	DomainIndexDirectory directory = DomainIndexDirectory.getIndexDirectory(name);
	return directory != null;
    }

    private LuceneDomainDirectory getIndexDirectory(String name) {
	DomainIndexDirectory directory = DomainIndexDirectory.getIndexDirectory(name);
	LuceneDomainDirectory domainDirectory = null;
	if (directory == null) {
	    domainDirectory = initIndex(name);
	} else {
	    domainDirectory = new LuceneDomainDirectory(directory);
	}
	return domainDirectory;
    }

    private LuceneDomainDirectory initIndex(String name) {
	DomainIndexDirectory directory = DomainIndexDirectory.createNewIndexDirectory(name);
	LuceneDomainDirectory domainDirectory = new LuceneDomainDirectory(directory);
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(domainDirectory, new StandardAnalyzer(), true, MaxFieldLength.UNLIMITED);
	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (LockObtainFailedException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} finally {
	    try {
		writer.close();
	    } catch (CorruptIndexException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return domainDirectory;
    }

    public void indexDomainObject(IndexDocument document) {
	String name = findDirectoryNameForClass(document.getDomainObjectClass());
	IndexWriter writer = null;
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(name);
	    writer = new IndexWriter(luceneDomainDirectory, new StandardAnalyzer(), false, MaxFieldLength.UNLIMITED);
	    writer.setMergeScheduler(new SerialMergeScheduler());

	    if (isIndexed(document, luceneDomainDirectory)) {
		unindexDomainObject(document, luceneDomainDirectory, writer);
	    }

	    writer.addDocument(generateLuceneDocument(document));

	    writer.optimize();

	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} finally {
	    if (writer != null) {
		try {
		    writer.close();
		} catch (CorruptIndexException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

    }

    public void indexDomainObjects(Collection<IndexDocument> documents) {

	Map<String, IndexWriter> writerMap = new HashMap<String, IndexWriter>();
	IndexWriter writer = null;
	try {
	    for (IndexDocument document : documents) {
		String className = findDirectoryNameForClass(document.getDomainObjectClass());
		LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(className);
		writer = writerMap.get(className);

		if (writer == null) {
		    writer = new IndexWriter(luceneDomainDirectory, new StandardAnalyzer(), false, MaxFieldLength.UNLIMITED);
		    writerMap.put(className, writer);
		    writer.setMergeScheduler(new SerialMergeScheduler());
		}
		if (isIndexed(document)) {
		    unindexDomainObject(document, luceneDomainDirectory, writer);
		}
		writer.addDocument(generateLuceneDocument(document));
	    }
	    for (IndexWriter writerToClose : writerMap.values()) {
		writerToClose.optimize();
		writerToClose.close();
	    }
	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} finally {
	    try {
		boolean foundWriter = false;
		for (IndexWriter writerToClose : writerMap.values()) {
		    writerToClose.close();
		    if (writerToClose == writer) {
			foundWriter = true;
		    }
		}
		if (writer != null && !foundWriter) {
		    writer.close();
		}
	    } catch (CorruptIndexException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

    }

    private boolean isIndexed(IndexDocument document, LuceneDomainDirectory luceneDomainDirectory) throws CorruptIndexException,
	    IOException, ParseException {

	Searcher searcher = new IndexSearcher(luceneDomainDirectory);
	QueryParser queryParser = new QueryParser(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), new StandardAnalyzer());
	Query query = queryParser.parse(document.getIndexId());

	TopDocs topDocs = searcher.search(query, 10);
	int length = topDocs.scoreDocs.length;
	if (length > 1) {
	    System.out.println("[WARN] Various hits for OID: " + document.getIndexId() + " hits:" + length);
	}

	return length > 0;
    }

    public boolean isIndexed(IndexDocument document) {
	String name = findDirectoryNameForClass(document.getDomainObjectClass());
	if (!hasIndexDirectory(name)) {
	    return false;
	}
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(name);
	    return isIndexed(document, luceneDomainDirectory);
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

    private void unindexDomainObject(IndexDocument document, LuceneDomainDirectory luceneDomainDirectory, IndexWriter writer)
	    throws CorruptIndexException, IOException, ParseException {
	QueryParser queryParser = new QueryParser(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), new StandardAnalyzer());
	Query query = queryParser.parse(document.getIndexId());
	writer.deleteDocuments(query);
    }

    public void unindexDomainObject(IndexDocument document) {
	String name = findDirectoryNameForClass(document.getDomainObjectClass());
	IndexWriter writer = null;
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(name);
	    writer = new IndexWriter(luceneDomainDirectory, new StandardAnalyzer(), false, MaxFieldLength.UNLIMITED);
	    writer.setMergeScheduler(new SerialMergeScheduler());
	    unindexDomainObject(document, luceneDomainDirectory, writer);
	    writer.optimize();

	} catch (CorruptIndexException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} catch (ParseException e) {
	    e.printStackTrace();
	    throw new DomainIndexException(e);
	} finally {
	    try {
		writer.close();
	    } catch (CorruptIndexException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

    }

    public <T extends DomainObject> List<T> search(Class<T> domainObjectClass, String value) {
	return search(domainObjectClass, DefaultIndexFields.DEFAULT_FIELD, value, DEFAULT_MAX_SIZE);
    }

    public <T extends DomainObject> List<T> search(Class<T> domainObjectClass, IndexableField searchField, String value) {
	return search(domainObjectClass, searchField, value, DEFAULT_MAX_SIZE);
    }

    public <T extends DomainObject> List<T> search(Class<T> domainObjectClass, IndexableField searchField, String value,
	    int maxHits) {

	if (StringUtils.isEmpty(value)) {
	    return Collections.emptyList();
	}
	return search(domainObjectClass, value, maxHits, searchField);
    }

    public <T extends DomainObject> List<T> search(Class<T> domainObjectClass, DSLState queryHelper, int maxHits) {

	if (queryHelper.isEmpty()) {
	    return Collections.<T> emptyList();
	}
	return search(domainObjectClass, queryHelper.finish(), maxHits, DefaultIndexFields.DEFAULT_FIELD);
    }

    public <T extends DomainObject> List<T> expertSearch(Class<T> domainObjectClass, String luceneQuery, int maxHits) {
	if (StringUtils.isEmpty(luceneQuery)) {
	    return Collections.<T> emptyList();
	}
	return search(domainObjectClass, luceneQuery, maxHits, DefaultIndexFields.DEFAULT_FIELD);
    }

    protected <T extends DomainObject> List<T> search(Class<T> domainObjectClass, String luceneQuery, int maxHits,
	    IndexableField defaultField) {
	String directoryName = findDirectoryNameForClass(domainObjectClass);
	if (!hasIndexDirectory(directoryName)) {
	    return Collections.emptyList();
	}

	List<T> domainObjects = new ArrayList<T>();
	try {
	    LuceneDomainDirectory luceneDomainDirectory = getIndexDirectory(directoryName);

	    Searcher searcher = new IndexSearcher(luceneDomainDirectory);
	    QueryParser queryParser = new QueryParser(defaultField.getFieldName(), new StandardAnalyzer());
	    Query query = queryParser.parse(luceneQuery);
	    BooleanQuery.setMaxClauseCount(getMaxClouseCount());

	    TopDocs topDocs = searcher.search(query, maxHits);

	    for (int i = 0; i < topDocs.scoreDocs.length; i++) {
		int docId = topDocs.scoreDocs[i].doc;
		Document doc = searcher.doc(docId);

		String OID = doc.get(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName());
		T domainObject = AbstractDomainObject.<T> fromExternalId(OID);
		domainObjects.add(domainObject);
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

	return superclass == AbstractDomainObject.class || superclass == OneBoxDomainObject.class ? domainObjectClass.getName()
		: findDirectoryNameForClass(superclass);
    }

    private Document generateLuceneDocument(IndexDocument indexDocument) {
	Document doc = new Document();
	StringBuilder builder = new StringBuilder();

	doc.add(new Field(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), indexDocument.getIndexId(), Field.Store.COMPRESS,
		Field.Index.NOT_ANALYZED));

	for (IndexableField indexableField : indexDocument.getIndexableFields()) {
	    String value = indexDocument.getValueForField(indexableField);
	    doc.add(new Field(indexableField.getFieldName(), value, Field.Store.NO, Field.Index.ANALYZED));
	    builder.append(value);
	    builder.append(" ");
	}
	doc.add(new Field(DefaultIndexFields.DEFAULT_FIELD.getFieldName(), builder.toString(), Field.Store.NO,
		Field.Index.ANALYZED));
	return doc;
    }
}
