package pt.ist.bennu.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.search.queryBuilder.dsl.DSLState;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.CharMatcher;

/**
 * DomainIndexer is the core of this plugin. This is a singleton class that
 * contains the methods to index, delete and search indexes for domain objects.
 * 
 * @author Paulo Abrantes
 */
public class DomainIndexer {

    public static final Version VERSION = Version.LUCENE_36;

    public static enum DefaultIndexFields implements IndexableField {
        DEFAULT_FIELD("all"), IDENTIFIER_FIELD("OID");

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

    public void indexDomainObject(IndexDocument document) {
        try (FSDirectory directory = getLuceneDomainDirectory(document.getIndexableClass(), true)) {
            try (StandardAnalyzer analyser = new StandardAnalyzer(VERSION)) {
                try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(VERSION, analyser))) {
                    QueryParser parser = new QueryParser(VERSION, DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), analyser);
                    writer.deleteDocuments(parser.parse(document.getIndexId()));
                    writer.addDocument(document.getLuceneDocument());
                }
            }
        } catch (ParseException e) {
            throw new DomainIndexException(e);
        } catch (IOException e) {
            throw new DomainIndexException(e);
        }
    }

    public void indexDomainObjects(Collection<IndexDocument> documents) {
        Map<Class<? extends Indexable>, Collection<IndexDocument>> map = new HashMap<>();
        for (IndexDocument document : documents) {
            if (!map.containsKey(document.getClass())) {
                map.put(document.getIndexableClass(), new ArrayList<IndexDocument>());
            }
            map.get(document.getClass()).add(document);
        }
        indexDomainObjects(map);
    }

    public void indexDomainObjects(Map<Class<? extends Indexable>, Collection<IndexDocument>> documents) {
        for (Entry<Class<? extends Indexable>, Collection<IndexDocument>> entry : documents.entrySet()) {
            try (FSDirectory directory = getLuceneDomainDirectory(entry.getKey(), true)) {
                try (StandardAnalyzer analyser = new StandardAnalyzer(VERSION)) {
                    try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(VERSION, analyser))) {
                        for (IndexDocument document : entry.getValue()) {
                            QueryParser parser =
                                    new QueryParser(VERSION, DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), analyser);
                            writer.deleteDocuments(parser.parse(document.getIndexId()));
                            writer.addDocument(document.getLuceneDocument());
                        }
                    }
                }
            } catch (ParseException e) {
                throw new DomainIndexException(e);
            } catch (IOException e) {
                throw new DomainIndexException(e);
            }
        }
    }

    public void unindexDomainObject(IndexDocument document) {
        try (FSDirectory directory = getLuceneDomainDirectory(document.getIndexableClass(), false)) {
            if (directory != null) {
                try (StandardAnalyzer analyser = new StandardAnalyzer(VERSION)) {
                    try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(VERSION, analyser))) {
                        QueryParser parser =
                                new QueryParser(VERSION, DefaultIndexFields.IDENTIFIER_FIELD.getFieldName(), analyser);
                        writer.deleteDocuments(parser.parse(document.getIndexId()));
                    }
                }
            }
        } catch (ParseException e) {
            throw new DomainIndexException(e);
        } catch (IOException e) {
            throw new DomainIndexException(e);
        }
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, IndexableField defaultField, String query, int maxHits) {
        try (StandardAnalyzer analyser = new StandardAnalyzer(VERSION)) {
            try (FSDirectory directory = getLuceneDomainDirectory(indexedClass, false)) {
                List<T> indexables = new ArrayList<>();
                if (directory != null) {
                    try (IndexReader reader = IndexReader.open(directory)) {
                        try (IndexSearcher searcher = new IndexSearcher(reader)) {
                            QueryParser parser = new QueryParser(VERSION, defaultField.getFieldName(), analyser);
                            TopDocs docs = searcher.search(parser.parse(query), maxHits);
                            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                                int docId = scoreDoc.doc;
                                String OID = searcher.doc(docId).get(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName());
                                indexables.add(FenixFramework.<T> getDomainObject(OID));
                            }
                        }
                    }
                }
                return indexables;
            }
        } catch (ParseException e) {
            throw new DomainIndexException(e);
        } catch (IOException e) {
            throw new DomainIndexException(e);
        }
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, IndexableField defaultField, String query) {
        return search(indexedClass, defaultField, query, DEFAULT_MAX_SIZE);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, String query, int maxHits) {
        return search(indexedClass, DefaultIndexFields.DEFAULT_FIELD, query, maxHits);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, String query) {
        return search(indexedClass, DefaultIndexFields.DEFAULT_FIELD, query, DEFAULT_MAX_SIZE);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, IndexableField defaultField, DSLState query, int maxHits) {
        return search(indexedClass, defaultField, query.finish(), maxHits);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, IndexableField defaultField, DSLState query) {
        return search(indexedClass, defaultField, query.finish(), DEFAULT_MAX_SIZE);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, DSLState query, int maxHits) {
        return search(indexedClass, DefaultIndexFields.DEFAULT_FIELD, query.finish(), maxHits);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, DSLState query) {
        return search(indexedClass, DefaultIndexFields.DEFAULT_FIELD, query.finish(), DEFAULT_MAX_SIZE);
    }

    private static FSDirectory getLuceneDomainDirectory(Class<? extends Indexable> indexableClass, boolean create)
            throws IOException {
        String basedir = ConfigurationManager.getProperty("lucene.indexing.directory");
        if (basedir == null) {
            throw new RuntimeException("Please specify 'lucene.indexing.directory' in configuration.properties");
        }
        Matcher matcher = Pattern.compile("(\\{.+?\\})").matcher(basedir);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replaceStr = CharMatcher.anyOf("{}").trimFrom(matcher.group());
            matcher.appendReplacement(result, System.getProperty(replaceStr));
        }
        matcher.appendTail(result);
        File file = new File(result.toString() + File.separator + indexableClass.getName());
        if (!file.isDirectory()) {
            if (!create) {
                return null;
            }
            file.mkdir();
        }
        return FSDirectory.open(file);
    }
}
