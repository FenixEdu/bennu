/*
 * @(#)ResponseWrapper.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.presentationTier.servlets.filters.requestWrappers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper {

    protected final HttpServletResponse httpServletResponse;

    protected BufferedFacadServletOutputStream bufferedFacadServletOutputStream = null;

    protected BufferedFacadPrintWriter bufferedFacadPrintWriter = null;

    public ResponseWrapper(final HttpServletResponse httpServletResponse) throws IOException {
	super(httpServletResponse);
	this.httpServletResponse = httpServletResponse;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
	if (bufferedFacadServletOutputStream == null) {
	    bufferedFacadServletOutputStream = new BufferedFacadServletOutputStream(httpServletResponse.getOutputStream());
	}
	return bufferedFacadServletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
	if (bufferedFacadPrintWriter == null) {
	    bufferedFacadPrintWriter = new BufferedFacadPrintWriter(httpServletResponse.getWriter());
	}
	return bufferedFacadPrintWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
	if (bufferedFacadServletOutputStream != null) {
	    bufferedFacadServletOutputStream.flush();
	}
	if (bufferedFacadPrintWriter != null) {
	    bufferedFacadPrintWriter.flush();
	}
    }

    public void writeRealResponse() throws IOException {
	if (bufferedFacadServletOutputStream != null) {
	    bufferedFacadServletOutputStream.writeRealResponse();
	}
	if (bufferedFacadPrintWriter != null) {
	    bufferedFacadPrintWriter.writeRealResponse();
	}
    }

}
