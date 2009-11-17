package myorg.util.lucene;

import java.util.ArrayList;
import java.util.List;

import myorg.domain.index.DomainIndexFile;
import myorg.domain.util.ByteArray;
import pt.ist.fenixWebFramework.services.Service;

public class RAMIndex implements IndexFile {

    static final int BUFFER_SIZE = 1024;

    private List<ByteArray> buffers;
    private long length;
    private long lastModified = System.currentTimeMillis();
    private DomainIndexFile domainIndexFile;
    private String name;

    public RAMIndex() {
	this.buffers = new ArrayList<ByteArray>();
    }

    public RAMIndex(DomainIndexFile domainIndexFile) {
	this();
	this.domainIndexFile = domainIndexFile;
	this.name = domainIndexFile.getName();
	this.length = domainIndexFile.getLength();
	Long lastModified2 = domainIndexFile.getLastModified();
	this.lastModified = lastModified2 != null ? lastModified2 :System.currentTimeMillis()  ;
	this.buffers = this.domainIndexFile.split(BUFFER_SIZE);
    }


    private void dumpBuffersToDomainIndex() {
	byte[] totalArray = new byte[BUFFER_SIZE * buffers.size()];
	int offset = 0;
	for (ByteArray array : buffers) {
	    byte[] bytes = array.getBytes();
	    if (bytes.length > BUFFER_SIZE) {
		System.out.println("buffer length is greater than buffer size...WTF!");
	    }
	    System.arraycopy(bytes, 0, totalArray, offset, Math.min(bytes.length, BUFFER_SIZE));
	    offset += BUFFER_SIZE;
	}
	this.domainIndexFile.setIndexContent(new ByteArray(totalArray));
    }

    public synchronized Long getLength() {
	return length;
    }

    public synchronized void setLength(long length) {
	this.length = length;
    }

    public synchronized Long getLastModified() {
	return lastModified;
    }

    public synchronized void setLastModified(long lastModified) {
	this.lastModified = lastModified;
    }

    public final synchronized ByteArray addBuffer(int size) {
	ByteArray buffer = newBuffer(size);
	buffers.add(buffer);
	return buffer;
    }

    final synchronized ByteArray getBuffer(int index) {
	return buffers.get(index);
    }

    final synchronized int numBuffers() {
	return buffers.size();
    }

    protected ByteArray newBuffer(int size) {
	return new ByteArray(new byte[size]);
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void setName(String name) {
	this.name = name;
    }

    @Override
    public void setLastModified(Long time) {
	this.lastModified = time;
    }

    @Override
    public void setLength(Long length) {
	this.length = length;
    }

    @Override
    public RAMIndex getNonPersistentIndex() {
	return this;
    }

    @Override
    public DomainIndexFile getPersistentIndex() {
	return this.domainIndexFile;
    }

    @Override
    public boolean isPersisted() {
	return false;
    }

    @Service
    public void persist() {
	dumpBuffersToDomainIndex();
	this.domainIndexFile.setName(getName());
	this.domainIndexFile.setLastModified(getLastModified());
	this.domainIndexFile.setLength(getLength());
    }
}
