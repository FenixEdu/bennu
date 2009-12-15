package myorg.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pt.utl.ist.fenix.tools.util.FileUtils;

public class InputStreamUtil {

    public static byte[] consumeInputStream(final InputStream inputStream) {
	byte[] result = null;
	if (inputStream != null) {
	    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    try {
		try {
		    FileUtils.copy(inputStream, byteArrayOutputStream);
		    byteArrayOutputStream.flush();
		    result = byteArrayOutputStream.toByteArray();
		    byteArrayOutputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    } finally {
		try {
		    inputStream.close();
		    byteArrayOutputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return result;
    }
}
