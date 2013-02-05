package pt.ist.fenixframework.plugins.luceneIndexing;

import java.io.IOException;

import org.apache.lucene.store.IndexInput;

import pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexFile;

public class DomainIndexInput extends IndexInput {
    static final int BUFFER_SIZE = 1024;

    private final long length;
    private final int numBuffers;

    private int currentBufferIndex;

    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    private final DomainIndexFile file;

    public DomainIndexInput(DomainIndexFile file) {
        super(file.getName());
        this.currentBufferIndex = -1;
        this.length = file.getLength();
        this.file = file;
        this.numBuffers = (int) (this.length / BUFFER_SIZE) + 1;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public long length() {
        return this.length;
    }

    @Override
    public byte readByte() throws IOException {

        if (bufferPosition >= bufferLength) {
            currentBufferIndex++;
            switchCurrentBuffer(true);
        }
        return this.file.readByte(currentBufferIndex * BUFFER_SIZE + bufferPosition++);
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
            System.arraycopy(this.file.getIndexContent(), currentBufferIndex * BUFFER_SIZE + bufferPosition, b, offset,
                    bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            bufferPosition += bytesToCopy;
        }

    }

    private final void switchCurrentBuffer(boolean enforceEOF) throws IOException {
        if (currentBufferIndex >= this.numBuffers) {
            // end of file reached, no more buffers left
            if (enforceEOF) {
                throw new IOException("Read past EOF");
            }
            // Force EOF if a read takes place at this position
            currentBufferIndex--;
            bufferPosition = BUFFER_SIZE;
        } else {
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
        if (this.currentBufferIndex > -1 || pos < bufferStart || pos >= bufferStart + BUFFER_SIZE) {
            currentBufferIndex = (int) (pos / BUFFER_SIZE);
            switchCurrentBuffer(false);
        }
        bufferPosition = (int) (pos % BUFFER_SIZE);
    }

}
