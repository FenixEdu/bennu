/*
 * @(#)BufferedFacadPrintWriter.java
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

package myorg.presentationTier.servlets.filters.requestWrappers;

import java.io.PrintWriter;

public class BufferedFacadPrintWriter extends PrintWriter {

    final StringBuilder stringBuilder = new StringBuilder();

    final PrintWriter printWriter;

    public BufferedFacadPrintWriter(final PrintWriter printWriter) {
	super(printWriter);
	this.printWriter = printWriter;
    }

    @Override
    public void write(final char[] cbuf) {
	stringBuilder.append(cbuf);
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) {
	stringBuilder.append(cbuf, off, len);
    }

    @Override
    public void write(final int c) {
	stringBuilder.append((char) c);
    }

    @Override
    public void write(final String str) {
	stringBuilder.append(str);
    }

    @Override
    public void write(final String str, final int off, final int len) {
	stringBuilder.append(str, off, len);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    public void writeRealResponse() {
	printWriter.write(stringBuilder.toString());
	printWriter.flush();
	printWriter.close();
    }

}
