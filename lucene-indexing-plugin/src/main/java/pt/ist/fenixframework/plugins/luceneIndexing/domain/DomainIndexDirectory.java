package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.store.LockObtainFailedException;

import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer.DomainIndexException;
import pt.ist.fenixframework.plugins.luceneIndexing.LuceneDomainDirectory;

public class DomainIndexDirectory extends DomainIndexDirectory_Base {

    public DomainIndexDirectory(String name) {
        super();
        setPluginRoot(LuceneSearchPluginRoot.getInstance());
        setIndexForClass(name);
    }

    public DomainIndexFile getIndexFile(String name) {
        for (DomainIndexFile file : getIndexFiles()) {
            if (file.getName().equals(name) && file.getIndexContent() != null) {
                return file;
            }
        }
        return null;
    }

    public LuceneDomainDirectory getLuceneDomainDirectory() {
        try {
            return new LuceneDomainDirectory(this);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DomainIndexException(e);
        }
    }

    public static boolean hasIndexDirectory(String name) {
        return getIndexDirectory(name) != null;
    }

    public static DomainIndexDirectory getIndexDirectory(String name) {
        return getIndexDirectory(name, false);
    }

    public static DomainIndexDirectory getIndexDirectory(String name, boolean create) {
        for (DomainIndexDirectory directory : LuceneSearchPluginRoot.getInstance().getDomainIndexDirectories()) {
            if (directory.getIndexForClass().equals(name)) {
                return directory;
            }
        }
        if (create) {
            DomainIndexDirectory directory = new DomainIndexDirectory(name);
            try {
                IndexWriter writer =
                        new IndexWriter(new LuceneDomainDirectory(directory), new IndexWriterConfig(DomainIndexer.VERSION,
                                new StandardAnalyzer(DomainIndexer.VERSION)).setOpenMode(OpenMode.CREATE).setMergeScheduler(
                                new SerialMergeScheduler()));
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
            return directory;
        }
        return null;
    }

    public void delete() {
        for (DomainIndexFile file : getIndexFilesSet()) {
            file.delete();
        }
        setPluginRoot(null);
        deleteDomainObject();
    }
    @Deprecated
    public java.util.Set<pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexFile> getIndexFiles() {
        return getIndexFilesSet();
    }

}
