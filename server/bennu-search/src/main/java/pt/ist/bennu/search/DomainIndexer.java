package pt.ist.bennu.search;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
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
    public static final Version VERSION = Version.LUCENE_44;

    public static final int DEFAULT_MAX_SIZE = Integer.MAX_VALUE;

    static final String FORCE_REINDEX_LIST = "force-reindex";

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

    public void registerForReindex(Set<Indexable> indexable) {
        Set<Indexable> reindex = FenixFramework.getTransaction().getFromContext(FORCE_REINDEX_LIST);
        if (reindex == null) {
            reindex = new HashSet<>();
        }
        reindex.addAll(indexable);
        FenixFramework.getTransaction().putInContext(FORCE_REINDEX_LIST, reindex);
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, Query query, int maxHits) {
        List<T> indexables = new ArrayList<>();
        try (StandardAnalyzer analyser = new StandardAnalyzer(VERSION)) {
            try (Directory directory = getLuceneDomainDirectory(indexedClass, false)) {
                if (directory != null) {
                    try (DirectoryReader reader = DirectoryReader.open(directory)) {
                        IndexSearcher searcher = new IndexSearcher(reader);
                        for (ScoreDoc doc : searcher.search(query, null, maxHits).scoreDocs) {
                            String OID = searcher.doc(doc.doc).get(DefaultIndexFields.IDENTIFIER_FIELD.getFieldName());
                            indexables.add(FenixFramework.<T> getDomainObject(OID));
                        }
                    }
                }
            } catch (IOException e) {
                throw new DomainIndexException(e);
            }
        }
        return indexables;
    }

    public <T extends Indexable> List<T> search(Class<T> indexedClass, IndexableField defaultField, String query, int maxHits) {
        try (StandardAnalyzer analyser = new StandardAnalyzer(VERSION)) {
            QueryParser parser = new QueryParser(VERSION, defaultField.getFieldName(), analyser);
            return search(indexedClass, parser.parse(normalize(query)), maxHits);
        } catch (ParseException e) {
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

    public static String normalize(String text) {
        return Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static FSDirectory getLuceneDomainDirectory(Class<? extends Indexable> indexableClass, boolean create)
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
