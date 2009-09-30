package myorg.domain.index;

import myorg.domain.MyOrg;
import myorg.domain.util.ByteArray;

public class IndexFile extends IndexFile_Base {

    public IndexFile() {
	super();
	setMyOrg(MyOrg.getInstance());
	setIndexMarker(0);
	setLength(0L);
    }

    public void delete() {
	removeDirectory();
	removeMyOrg();
	for (IndexFileBuffer buffer : getIndexFileBuffers()) {
	    buffer.delete();
	}
	deleteDomainObject();
    }

    public Integer getIndexMarkerAndIncrement() {
	Integer currentIndex = getIndexMarker();
	setIndexMarker(currentIndex + 1);
	return currentIndex;
    }

    public IndexFileBuffer addBuffer(Integer size) {
	IndexFileBuffer buffer = new IndexFileBuffer(this);
	ByteArray array = new ByteArray(new byte[size]);
	buffer.setBuffer(array);
	return buffer;
    }

    public IndexFileBuffer getBuffer(Integer index) {
	for (IndexFileBuffer buffer : getIndexFileBuffers()) {
	    if (buffer.getIndexMarker().intValue() == index.intValue()) {
		return buffer;
	    }
	}
	return null;
    }

}
