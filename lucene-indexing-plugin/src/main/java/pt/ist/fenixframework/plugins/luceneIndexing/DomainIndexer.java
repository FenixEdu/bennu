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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexDirectory;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.IndexDocument;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.DSLState;

/**
 * DomainIndexer is the core of this plugin. This is a singleton class that
 * contains the methods to index, delete and search indexes for domain objects.
 * 
 * @author Paulo Abrantes
 */
public class DomainIndexer {

    public static final Version VERSION = Version.LUCENE_35;

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

    public void indexDomainObject(IndexDocument document) {
        String name = findDirectoryNameForClass(document.getDomainObjectClass());
        IndexWriter writer = null;
        try {
            LuceneDomainDirectory luceneDomainDirectory =
                    DomainIndexDirectory.getIndexDirectory(name, true).getLuceneDomainDirectory();
            writer =
                    new IndexWriter(luceneDomainDirectory, new IndexWriterConfig(VERSION, new StandardAnalyzer(VERSION))
                            .setOpenMode(OpenMode.CREATE_OR_APPEND).setMergeScheduler(new SerialMergeScheduler()));

            if (isIndexed(document, luceneDomainDirectory)) {
                unindexDomainObject(document, writer);
            }

            writer.addDocument(generateLuceneDocument(document));
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
                LuceneDomainDirectory luceneDomainDirectory =
                        DomainIndexDirectory.getIndexDirectory(className, true).getLuceneDomainDirectory();
                writer = writerMap.get(className);

                if (writer == null) {
                    writer =
                            new IndexWriter(luceneDomainDirectory, new IndexWriterConfig(VERSION, new StandardAnalyzer(VERSION))
                                    .setOpenMode(OpenMode.CREATE_OR_APPEND).setMergeScheduler(new SerialMergeScheduler()));
                    writerMap.put(className, writer);
                }
                if (isIndexed(document, luceneDomainDirectory)) {
                    unindexDomainObject(document, writer);
                }
                writer.addDocument(generateLuceneDocument(document));
            }
            for (IndexWriter writerToClose : writerMap.values()) {
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

    private static boolean isIndexed(IndexDocument document, LuceneDomainDirectory luceneDomainDirectory)
            throws CorruptIndexException, IOException, ParseException {
        IndexSearcher searcher = new IndexSearcher(IndexReader.open(luceneDomainDirectory, true));

        QueryParser queryParser =
                new QueryParser(VERSION, DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), new StandardAnalyzer(VERSION));
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
        if (!DomainIndexDirectory.hasIndexDirectory(name)) {
            return false;
        }
        try {
            LuceneDomainDirectory luceneDomainDirectory = DomainIndexDirectory.getIndexDirectory(name).getLuceneDomainDirectory();
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

    private static void unindexDomainObject(IndexDocument document, IndexWriter writer) throws CorruptIndexException,
            IOException, ParseException {
        QueryParser queryParser =
                new QueryParser(VERSION, DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), new StandardAnalyzer(VERSION));
        Query query = queryParser.parse(document.getIndexId());
        writer.deleteDocuments(query);
    }

    public void unindexDomainObject(IndexDocument document) {
        String name = findDirectoryNameForClass(document.getDomainObjectClass());
        IndexWriter writer = null;
        try {
            LuceneDomainDirectory luceneDomainDirectory =
                    DomainIndexDirectory.getIndexDirectory(name, true).getLuceneDomainDirectory();
            writer =
                    new IndexWriter(luceneDomainDirectory, new IndexWriterConfig(VERSION, new StandardAnalyzer(VERSION))
                            .setOpenMode(OpenMode.CREATE_OR_APPEND).setMergeScheduler(new SerialMergeScheduler()));
            unindexDomainObject(document, writer);
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
                if (writer != null) {
                    writer.close();
                }
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
        if (!DomainIndexDirectory.hasIndexDirectory(directoryName)) {
            return Collections.emptyList();
        }

        List<T> domainObjects = new ArrayList<T>();
        try {
            LuceneDomainDirectory luceneDomainDirectory =
                    DomainIndexDirectory.getIndexDirectory(directoryName, true).getLuceneDomainDirectory();

            IndexSearcher searcher = new IndexSearcher(IndexReader.open(luceneDomainDirectory, true));

            QueryParser queryParser = new QueryParser(VERSION, defaultField.getFieldName(), new StandardAnalyzer(VERSION));
            Query query = queryParser.parse(luceneQuery);
            BooleanQuery.setMaxClauseCount(getMaxClouseCount());

            TopDocs topDocs = searcher.search(query, maxHits);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docId = scoreDoc.doc;
                Document doc = searcher.doc(docId);

                String OID = doc.get(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName());
                T domainObject = FenixFramework.getDomainObject(OID);
                if (domainObject != null && domainObject.getClass().isAssignableFrom(domainObjectClass)) {
                    domainObjects.add(domainObject);
                }
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
        return findDirectoryNameForDomainClass(DomainModelUtil.getDomainClassFor(domainObjectClass));
    }

    private String findDirectoryNameForDomainClass(DomainClass domainClass) {
        if (domainClass.getSuperclass() == null) {
            return domainClass.getFullName();
        } else {
            return findDirectoryNameForDomainClass((DomainClass) domainClass.getSuperclass());
        }
    }

    private static Document generateLuceneDocument(IndexDocument indexDocument) {
        Document doc = new Document();
        StringBuilder builder = new StringBuilder();

        doc.add(new Field(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), indexDocument.getIndexId(), Field.Store.YES,
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
