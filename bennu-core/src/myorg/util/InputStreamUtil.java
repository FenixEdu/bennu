/* 
* @(#)InputStreamUtil.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package myorg.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pt.utl.ist.fenix.tools.util.FileUtils;

/**
 * 
 * @author  Luis Cruz
 * @author  Paulo Abrantes
 * 
*/
public class InputStreamUtil {

    public static final int DEFAULT_BUFFER_SIZE = 1024;

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

    public static int copyStream(InputStream source, OutputStream target) throws IOException {
	return copyStream(source, target, DEFAULT_BUFFER_SIZE);
    }

    public static int copyStream(InputStream source, OutputStream target, int bufferSize) throws IOException {

	int countBytesRead = -1;
	int totalBytes = 0;
	byte[] bufferCopy = new byte[bufferSize];
	while ((countBytesRead = source.read(bufferCopy)) != -1) {
	    target.write(bufferCopy, 0, countBytesRead);
	    totalBytes += countBytesRead;
	}

	target.flush();
	return totalBytes;
    }
}
