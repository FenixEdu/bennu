package myorg.domain.index;

import myorg.domain.MyOrg;
import myorg.domain.util.ByteArray;

public class IndexFileBuffer extends IndexFileBuffer_Base {

    public IndexFileBuffer(IndexFile indexFile) {
	this.setIndexFile(indexFile);
	this.setIndexMarker(indexFile.getIndexMarkerAndIncrement());
	setMyOrg(MyOrg.getInstance());
    }

    public int getLength() {
	return getBuffer().getBytes().length;
    }

    public void writeByteAt(int pos, byte b) {
	ByteArray buffer = getBuffer();
	byte[] bytes = buffer.getBytes();
	bytes[pos] = b;
	buffer.setBytes(bytes);
	setBuffer(buffer);
    }

    public byte getByteAt(int bytePosition) {
	return getBuffer().getBytes()[bytePosition];
    }

    public void delete() {
	removeIndexFile();
	removeMyOrg();
	deleteDomainObject();
    }

}
