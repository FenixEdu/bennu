package myorg.util.lucene;

import java.io.IOException;

import myorg.domain.index.IndexFile;
import myorg.domain.index.IndexFileBuffer;

import org.apache.lucene.store.IndexInput;

public class DomainIndexInput extends IndexInput {
    static final int BUFFER_SIZE = 1024;

    private long length;

    private int currentBufferIndex;

    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    IndexFile file;
    IndexFileBuffer currentBuffer;

    public DomainIndexInput() {
	this.file = new IndexFile();
    }

    public DomainIndexInput(IndexFile file) {
	this.file = file;
	currentBufferIndex = -1;
	currentBuffer = null;
	this.length = this.file.getLength();
    }

    @Override
    public void close() throws IOException {
	// do nothing

    }

    @Override
    public long length() {
	return this.file.getLength();
    }

    @Override
    public byte readByte() throws IOException {
	if (bufferPosition >= bufferLength) {
	    currentBufferIndex++;
	    switchCurrentBuffer(true);
	}
	return currentBuffer.getByteAt(bufferPosition++);
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
	while (len > 0) {
	    if (bufferPosition >= bufferLength) {
		currentBufferIndex++;
		switchCurrentBuffer(true);
	    }

	    int remainInBuffer = bufferLength - bufferPosition;
	    int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
	    System.arraycopy(currentBuffer.getBuffer().getBytes(), bufferPosition, b, offset, bytesToCopy);
	    offset += bytesToCopy;
	    len -= bytesToCopy;
	    bufferPosition += bytesToCopy;
	}
    }

    private final void switchCurrentBuffer(boolean enforceEOF) throws IOException {
	if (currentBufferIndex >= this.file.getIndexFileBuffersCount()) {
	    // end of file reached, no more buffers left
	    if (enforceEOF)
		throw new IOException("Read past EOF");
	    else {
		// Force EOF if a read takes place at this position
		currentBufferIndex--;
		bufferPosition = BUFFER_SIZE;
	    }
	} else {
	    currentBuffer = file.getBuffer(currentBufferIndex);
	    bufferPosition = 0;
	    bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
	    long buflen = length - bufferStart;
	    bufferLength = buflen > BUFFER_SIZE ? BUFFER_SIZE : (int) buflen;
	}
    }

    @Override
    public long getFilePointer() {
	return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
    }

    @Override
    public void seek(long pos) throws IOException {
	if (currentBuffer == null || pos < bufferStart || pos >= bufferStart + BUFFER_SIZE) {
	    currentBufferIndex = (int) (pos / BUFFER_SIZE);
	    switchCurrentBuffer(false);
	}
	bufferPosition = (int) (pos % BUFFER_SIZE);
    }

}
