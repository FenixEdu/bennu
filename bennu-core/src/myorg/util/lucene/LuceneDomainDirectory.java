package myorg.util.lucene;

import java.io.FileNotFoundException;
import java.io.IOException;

import myorg.domain.index.IndexDirectory;
import myorg.domain.index.IndexFile;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.SingleInstanceLockFactory;

public class LuceneDomainDirectory extends Directory {

    private IndexDirectory directory;

    public LuceneDomainDirectory(IndexDirectory directory) {
	setLockFactory(new SingleInstanceLockFactory());
	this.directory = directory;
    }

    @Override
    public void close() throws IOException {
	this.directory = null;
    }

    @Override
    public IndexOutput createOutput(String name) throws IOException {
	IndexFile file = this.directory.getIndexFile(name);
	if (file != null) {
	    file.removeDirectory();
	    // On the original RamDirectory the file isn't being deleted
	    // but probably only because since it's RAM will be flushed
	    // as soon the application stops. I'm deleting it explicity
	    // from the database in order to avoid garbage.
	    // Paulo Abrantes - 18/07/2009

	    file.delete();
	}

	file = new IndexFile();
	file.setName(name);
	file.setDirectory(this.directory);

	return new DomainIndexOutput(file);
    }

    @Override
    public void deleteFile(String name) throws IOException {
	IndexFile indexFile = this.directory.getIndexFile(name);
	if (indexFile != null) {
	    indexFile.delete();
	} else {
	    throw new FileNotFoundException(name);
	}

    }

    @Override
    public boolean fileExists(String name) throws IOException {
	return this.directory.getIndexFile(name) != null;
    }

    @Override
    public long fileLength(String name) throws IOException {
	IndexFile file = this.directory.getIndexFile(name);
	if (file == null) {
	    throw new FileNotFoundException(name);
	}
	return file.getLength();
    }

    @Override
    public long fileModified(String name) throws IOException {
	IndexFile file = this.directory.getIndexFile(name);
	if (file == null) {
	    throw new FileNotFoundException(name);
	}
	return file.getLastModified();
    }

    @Override
    public String[] list() throws IOException {
	String[] list = new String[this.directory.getIndexFilesCount()];
	int i = 0;
	for (IndexFile file : this.directory.getIndexFiles()) {
	    list[i++] = file.getName();
	}
	return list;
    }

    @Override
    public IndexInput openInput(String name) throws IOException {
	IndexFile file = this.directory.getIndexFile(name);
	if (file == null) {
	    throw new FileNotFoundException(name);
	}
	return new DomainIndexInput(file);
    }

    @Override
    public void renameFile(String oldName, String newName) throws IOException {
	IndexFile fromFile = this.directory.getIndexFile(oldName);
	if (fromFile == null) {
	    throw new FileNotFoundException(oldName);
	}
	IndexFile toFile = this.directory.getIndexFile(newName);
	if (toFile != null) {
	    toFile.delete();
	}
	fromFile.setName(newName);
    }

    @Override
    public void touchFile(String name) throws IOException {
	IndexFile file = this.directory.getIndexFile(name);

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

}
