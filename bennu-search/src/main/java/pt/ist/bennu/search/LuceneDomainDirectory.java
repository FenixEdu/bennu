package pt.ist.bennu.search;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.SingleInstanceLockFactory;

import pt.ist.bennu.search.domain.DomainIndexDirectory;
import pt.ist.bennu.search.domain.DomainIndexFile;

public class LuceneDomainDirectory extends Directory {

    private DomainIndexDirectory directory;
    private Map<String, RAMIndex> workingIndexes;

    public LuceneDomainDirectory(DomainIndexDirectory directory) throws IOException {
        setLockFactory(new SingleInstanceLockFactory());
        this.directory = directory;
        this.workingIndexes = new HashMap<String, RAMIndex>();
    }

    public DomainIndexDirectory getDomainIndexDirectory() {
        return this.directory;
    }

    @Override
    public void close() throws IOException {
        this.directory = null;
        this.workingIndexes.clear();
        this.workingIndexes = null;
    }

    public IndexFile getFile(String name) {
        IndexFile file = workingIndexes.get(name);
        if (file != null) {
            return file;
        }

        return this.directory.getIndexFile(name);
    }

    @Override
    public IndexOutput createOutput(String name) throws IOException {
        IndexFile file = getFile(name);
        if (file != null) {
            removeFile(file);
        }

        file = new DomainIndexFile();
        file.setName(name);
        ((DomainIndexFile) file).setDirectory(this.directory);

        return new DomainIndexOutput(file.getNonPersistentIndex(), this);
    }

    private void removeFile(IndexFile file) {
        if (file.isPersisted()) {
            DomainIndexFile domainFile = (DomainIndexFile) file;
            domainFile.removeDirectory();
            // On the original RamDirectory the file isn't being deleted
            // but probably only because since it's RAM will be flushed
            // as soon the application stops. I'm deleting it explicitly
            // from the database in order to avoid garbage.
            // Paulo Abrantes - 18/07/2009

            domainFile.delete();
        } else {
            workingIndexes.remove(file.getName());
            ((RAMIndex) file).getPersistentIndex().delete();
        }
    }

    @Override
    public void deleteFile(String name) throws IOException {
        IndexFile indexFile = getFile(name);
        if (indexFile != null) {
            removeFile(indexFile);
        } else {
            throw new FileNotFoundException(name);
        }

    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return getFile(name) != null;
    }

    @Override
    public long fileLength(String name) throws IOException {
        IndexFile file = getFile(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.getLength();
    }

    @Override
    public long fileModified(String name) throws IOException {
        IndexFile file = getFile(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.getLastModified();
    }

    @Override
    public String[] listAll() throws IOException {
        Set<String> files = new HashSet<String>();
        files.addAll(workingIndexes.keySet());
        for (DomainIndexFile file : this.directory.getIndexFiles()) {
            if (file.getIndexContent() != null) {
                files.add(file.getName());
            }
        }
        int size = files.size();
        int i = 0;
        String[] list = new String[size];
        for (String fileName : files) {
            list[i++] = fileName;
        }
        return list;
    }

    @Override
    public IndexInput openInput(String name) throws IOException {
        IndexFile file = getFile(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return new DomainIndexInput(file.getPersistentIndex());
    }

    @Override
    public void touchFile(String name) throws IOException {
        IndexFile file = getFile(name);

        if (file == null) {
            throw new FileNotFoundException(name);
        }

        long ts2, ts1 = System.currentTimeMillis();
        do {
            try {
                Thread.sleep(0, 1);
            } catch (InterruptedException e) {
            }
            ts2 = System.currentTimeMillis();
        } while (ts1 == ts2);

        file.setLastModified(ts2);
    }

    public void removeFileFromMap(RAMIndex ramIndex) {
        workingIndexes.remove(ramIndex);
    }
}
