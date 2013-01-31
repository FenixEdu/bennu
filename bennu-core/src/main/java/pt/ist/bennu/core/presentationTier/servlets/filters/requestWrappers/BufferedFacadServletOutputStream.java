/* 
* @(#)BufferedFacadServletOutputStream.java 
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
package pt.ist.bennu.core.presentationTier.servlets.filters.requestWrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class BufferedFacadServletOutputStream extends ServletOutputStream {

	final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	final OutputStream realOutputStream;

	public BufferedFacadServletOutputStream(final OutputStream realOutputStream) {
		this.realOutputStream = realOutputStream;
	}

	@Override
	public void write(final int value) throws IOException {
		byteArrayOutputStream.write(value);
	}

	@Override
	public void write(final byte[] value) throws IOException {
		byteArrayOutputStream.write(value);
	}

	@Override
	public void write(final byte[] value, final int off, final int len) throws IOException {
		byteArrayOutputStream.write(value, off, len);
	}

	public void writeRealResponse() throws IOException {
		realOutputStream.write(byteArrayOutputStream.toByteArray());
		realOutputStream.flush();
		realOutputStream.close();
	}

}
