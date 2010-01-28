package pt.ist.fenixframework.plugins.luceneIndexing.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixframework.plugins.luceneIndexing.IndexFile;
import pt.ist.fenixframework.plugins.luceneIndexing.RAMIndex;
import pt.ist.fenixframework.plugins.luceneIndexing.util.ByteArray;

public class DomainIndexFile extends DomainIndexFile_Base implements IndexFile {

    public DomainIndexFile() {
	super();
	setPluginRoot(LuceneSearchPluginRoot.getInstance());
	setLength(0L);
    }

    public void delete() {
	removeDirectory();
	removePluginRoot();
	setIndexContent(null);
	deleteDomainObject();
    }

    @Override
    public RAMIndex getNonPersistentIndex() {
	return new RAMIndex(this);
    }

    @Override
    public DomainIndexFile getPersistentIndex() {
	return this;
    }

    @Override
    public boolean isPersisted() {
	return true;
    }

    public List<ByteArray> split(int bufferSize) {
	long length = getLength();
	ByteArray indexContent = getIndexContent();
	ArrayList<ByteArray> buffers = new ArrayList<ByteArray>();

	byte[] content = indexContent != null ? indexContent.getBytes() : new byte[Math.min((int) length, bufferSize)];
	int runner = 0;

	while (runner < length) {
	    byte[] buffer = new byte[bufferSize];
	    System.arraycopy(content, runner, buffer, 0, Math.min(content.length, bufferSize));
	    buffers.add(new ByteArray(buffer));
	    runner += bufferSize;
	}

	return buffers;
    }

    /*
     * This means that the max size we can store is a 2Gb index file. Since
     * we're casting from long to int.
     */
    public byte readByte(long pos) throws IOException {
	return getIndexContent().getBytes()[(int) pos];
    }

    public void readBytes(long pos, byte[] b, int offset, int len) throws IOException {
	System.arraycopy(getIndexContent().getBytes(), (int) pos, b, offset, len);
    }

}
