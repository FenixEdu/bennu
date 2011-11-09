/*
 * @(#)WriteCustomTask.java
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

package myorg.domain.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;

public abstract class WriteCustomTask extends ReadCustomTask {

    private Set<GenericFile> outputFiles;

    void setOutputFiles(final Set<GenericFile> outputFiles) {
	this.outputFiles = outputFiles;
    }

    protected abstract void doService();

    @Override
    public final void doIt() {
	callService();
    }

    @Service
    private void callService() {
	doService();
    }

    protected void storeFileOutput(final String displayName, final String filename,
	    final byte[] content, final String contentType) {
	final CustomTaskOutputFile outputFile = new CustomTaskOutputFile();
	outputFile.setDisplayName(displayName);
	outputFile.setFilename(filename);
	outputFile.setContent(content);
	outputFile.setContentType(contentType);
	outputFiles.add(outputFile);
    }

    protected void storeFileOutput(final String displayName, final String filename,
	    final Spreadsheet spreadsheet) {
	try {
	    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    spreadsheet.exportToXLSSheet(outputStream);
	    storeFileOutput(displayName, filename, outputStream.toByteArray(), "application/vnd.ms-excel");
	} catch (final IOException e) {
	    throw new Error(e);
	}
    }

}
