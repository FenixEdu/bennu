/*
 * @(#)ByteArray.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
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

package myorg.domain.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import pt.utl.ist.fenix.tools.util.FileUtils;

public class ByteArray implements Serializable {

    private byte[] bytes;

    public ByteArray(final InputStream stream) throws IOException {
	final ByteArrayOutputStream output = new ByteArrayOutputStream();
	FileUtils.copy(stream, output);
	this.bytes = output.toByteArray();
    }
    
    public ByteArray(byte[] value) {        
        this.bytes = value;
    }

    public byte[] getBytes() {
	return bytes == null ? null : bytes.clone();
    }

    public static byte[] toBytes(final InputStream stream) throws IOException {
	final ByteArray byteArray = new ByteArray(stream);
	return byteArray.getBytes();
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
