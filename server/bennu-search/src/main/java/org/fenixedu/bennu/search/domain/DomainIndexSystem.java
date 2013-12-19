package org.fenixedu.bennu.search.domain;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.io.domain.LocalFileSystemStorage;
import org.fenixedu.bennu.search.IndexDocument.DefaultIndexFields;
import org.fenixedu.bennu.search.Indexable;
import org.fenixedu.bennu.search.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class DomainIndexSystem extends DomainIndexSystem_Base {
    private static Logger logger = LoggerFactory.getLogger(DomainIndexSystem.class);

    public static final Version VERSION = Version.LUCENE_44;

    public static final String FORCE_REINDEX_LIST = "force-reindex";

    public static class DomainIndexException extends RuntimeException {
        private static final long serialVersionUID = -4885892671487406190L;

        public DomainIndexException(Exception e) {
            super(e);
        }
    }

    private DomainIndexSystem() {
        super();
        setBennu(Bennu.getInstance());
        logger.info("Bootstrapped Indexing System");
        setStorage(new LocalFileSystemStorage("indexStorage", System.getProperty("java.io.tmpdir") + File.separatorChar
                + "indexStorage", 3));
    }

    public static DomainIndexSystem getInstance() {
        if (Bennu.getInstance().getIndexSystem() == null) {
            return create();
        }
        return Bennu.getInstance().getIndexSystem();
    }

    @Atomic(mode = TxMode.WRITE)
    private static DomainIndexSystem create() {
        if (Bennu.getInstance().getIndexSystem() == null) {
            return new DomainIndexSystem();
        }
        return Bennu.getInstance().getIndexSystem();
    }

    public static void registerForReindex(Set<Indexable> indexable) {
        Set<Indexable> reindex = FenixFramework.getTransaction().getFromContext(FORCE_REINDEX_LIST);
        if (reindex == null) {
            reindex = new HashSet<>();
        }
        reindex.addAll(indexable);
        FenixFramework.getTransaction().putInContext(FORCE_REINDEX_LIST, reindex);
    }

    public static <T extends Indexable> List<T> search(Class<T> indexedClass, Query query, int maxHits, Sort sort) {
        List<T> indexables = new ArrayList<>();
        try (StandardAnalyzer analyzer = new StandardAnalyzer(VERSION)) {
            try (Directory directory = getLuceneDomainDirectory(indexedClass, false)) {
                if (directory != null) {
                    try (DirectoryReader reader = DirectoryReader.open(directory)) {
                        IndexSearcher searcher = new IndexSearcher(reader);
                        for (ScoreDoc doc : searcher.search(query, maxHits, sort).scoreDocs) {
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

    public static <T extends Indexable> List<T> search(Class<T> type, Query query, int maxHits) {
        List<T> indexables = new ArrayList<>();
        try (StandardAnalyzer analyzer = new StandardAnalyzer(VERSION)) {
            try (Directory directory = getLuceneDomainDirectory(type, false)) {
                if (directory != null) {
                    try (DirectoryReader reader = DirectoryReader.open(directory)) {
                        IndexSearcher searcher = new IndexSearcher(reader);
                        for (ScoreDoc doc : searcher.search(query, maxHits).scoreDocs) {
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

    public static Query parse(String query, IndexableField defaultField) {
        try (StandardAnalyzer analyzer = new StandardAnalyzer(VERSION)) {
            QueryParser parser = new QueryParser(VERSION, defaultField.getFieldName(), analyzer);
            return parser.parse(normalize(query));
        } catch (ParseException e) {
            throw new DomainIndexException(e);
        }
    }

    public static List<String> tokenizeString(IndexableField field, String string) {
        List<String> result = new ArrayList<>();
        try (StandardAnalyzer analyzer = new StandardAnalyzer(VERSION)) {
            try (TokenStream stream = analyzer.tokenStream(field.getFieldName(), new StringReader(normalize(string)))) {
                stream.reset();
                while (stream.incrementToken()) {
                    result.add(stream.getAttribute(CharTermAttribute.class).toString());
                }
            } catch (IOException e) {
                // not thrown b/c we're using a string reader...
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static String normalize(String text) {
        return Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static FSDirectory getLuceneDomainDirectory(Class<? extends Indexable> indexableClass, boolean create)
            throws IOException {
        String path = DomainIndexSystem.getInstance().getStorage().getAbsolutePath() + "indexes/";
        File file = new File(path + indexableClass.getName());
        if (!file.isDirectory()) {
            if (!create) {
                return null;
            }
            file.mkdir();
        }
        return FSDirectory.open(file);
    }
}
