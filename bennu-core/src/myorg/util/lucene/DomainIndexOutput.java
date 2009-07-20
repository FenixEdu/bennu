package myorg.util.lucene;

import java.io.IOException;

import myorg.domain.index.IndexFile;
import myorg.domain.index.IndexFileBuffer;
import myorg.domain.util.ByteArray;

import org.apache.lucene.store.IndexOutput;

public class DomainIndexOutput extends IndexOutput {

    static final int BUFFER_SIZE = 1024;

    private int currentBufferIndex;

    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    private IndexFile file;
    private IndexFileBuffer currentBuffer;

    public DomainIndexOutput(IndexFile file) {
	this.file = file;

	currentBufferIndex = -1;
	currentBuffer = null;
    }

    @Override
    public void close() throws IOException {
	flush();
    }

    @Override
    public void flush() throws IOException {
	this.file.setLastModified(System.currentTimeMillis());
	setFileLength();
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
	if (currentBufferIndex == this.file.getIndexFileBuffersCount()) {
	    currentBuffer = this.file.addBuffer(BUFFER_SIZE);
	} else {
	    currentBuffer = this.file.getBuffer(currentBufferIndex);
	}
	bufferPosition = 0;
	bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
	bufferLength = currentBuffer.getLength();
    }

    @Override
    public void writeByte(byte b) throws IOException {
	if (bufferPosition == bufferLength) {
	    currentBufferIndex++;
	    switchCurrentBuffer();
	}
	currentBuffer.writeByteAt(bufferPosition++, b);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int len) throws IOException {
	assert b != null;
	while (len > 0) {
	    if (bufferPosition == bufferLength) {
		currentBufferIndex++;
		switchCurrentBuffer();
	    }

	    int remainInBuffer = currentBuffer.getLength() - bufferPosition;
	    int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;

	    /*
	     * We need this to persist right?
	     */
	    ByteArray byteArray = this.currentBuffer.getBuffer();
	    byte[] byteArrayToBeDestination = byteArray.getBytes();
	    System.arraycopy(b, offset, byteArrayToBeDestination, bufferPosition, bytesToCopy);
	    byteArray.setBytes(byteArrayToBeDestination);
	    this.currentBuffer.setBuffer(byteArray);

	    offset += bytesToCopy;
	    len -= bytesToCopy;
	    bufferPosition += bytesToCopy;
	}
    }

}
