package pt.ist.fenixframework.util;

import java.io.Serializable;

public class ByteArray implements Serializable {

    private byte[] bytes;

    public ByteArray(byte[] value) {
	this.bytes = value;
    }

    public byte[] getBytes() {
	return bytes;
    }

    public void setBytes(byte[] bytes) {
	this.bytes = bytes;
    }
}
