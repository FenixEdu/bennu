package myorg.util.lucene;

import java.io.IOException;

import org.apache.lucene.store.IndexOutput;

public class DomainIndexOutput extends IndexOutput {

    static final int BUFFER_SIZE = 1024;

    private int currentBufferIndex;

    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    private RAMIndex file;
    private byte[] currentBuffer;
    private LuceneDomainDirectory directory;

    public DomainIndexOutput(RAMIndex file, LuceneDomainDirectory directory) {
	this.file = file;

	this.currentBufferIndex = -1;
	this.currentBuffer = null;
	this.directory = directory;
    }

    @Override
    public void close() throws IOException {
	if (this.directory != null) {
	    this.directory.removeFileFromMap(this.file);
	}
	flush();
    }

    @Override
    public void flush() throws IOException {
	this.file.setLastModified(System.currentTimeMillis());
	setFileLength();
	this.file.persist();
    }

    private void setFileLength() {
	long pointer = bufferStart + bufferPosition;
	if (pointer > this.file.getLength()) {
	    file.setLength(pointer);
	}
    }

    @Override
    public long getFilePointer() {
	return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
    }

    @Override
    public long length() throws IOException {
	return this.file.getLength();
    }

    @Override
    public void seek(long pos) throws IOException {
	setFileLength();
	if (pos < bufferStart || pos >= bufferStart + bufferLength) {
	    currentBufferIndex = (int) (pos / BUFFER_SIZE);
	    switchCurrentBuffer();
	}

	bufferPosition = (int) (pos % BUFFER_SIZE);
    }

    private final void switchCurrentBuffer() throws IOException {
	if (currentBufferIndex == this.file.numBuffers()) {
	    currentBuffer = this.file.addBuffer(BUFFER_SIZE).getBytes();
	} else {
	    currentBuffer = this.file.getBuffer(currentBufferIndex).getBytes();
	}
	bufferPosition = 0;
	bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
	bufferLength = currentBuffer.length;
    }

    @Override
    public void writeByte(byte b) throws IOException {
	if (bufferPosition == bufferLength) {
	    currentBufferIndex++;
	    switchCurrentBuffer();
	}
	currentBuffer[bufferPosition++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int len) throws IOException {
	assert b != null;
	while (len > 0) {
	    if (bufferPosition == bufferLength) {
		currentBufferIndex++;
		switchCurrentBuffer();
	    }

	    int remainInBuffer = currentBuffer.length - bufferPosition;
	    int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;

	    byte[] byteArrayToBeDestination = this.currentBuffer;
	    System.arraycopy(b, offset, byteArrayToBeDestination, bufferPosition, bytesToCopy);

	    offset += bytesToCopy;
	    len -= bytesToCopy;
	    bufferPosition += bytesToCopy;
	}
    }

}
