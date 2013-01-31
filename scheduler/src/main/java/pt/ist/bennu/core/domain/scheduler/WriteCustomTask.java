/*
 * @(#)WriteCustomTask.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Scheduler Module.
 *
 *   The Scheduler Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Scheduler Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Scheduler Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.core.domain.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;

/**
 * 
 * @author Luis Cruz
 * 
 */
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
		try {
			if (getServerName() != null) {
				VirtualHost.setVirtualHostForThread(getServerName().toLowerCase());
			}
			doService();
		} finally {
			VirtualHost.releaseVirtualHostFromThread();
		}
	}

	/**
	 * Convenience method to more easily use VirtualHosts in these tasks
	 * 
	 * @return the String with the server name of the VirtualHost to use when
	 *         executing this task
	 */
	public String getServerName() {
		return null;
	}

	protected void storeFileOutput(final String displayName, final String filename, final byte[] content, final String contentType) {
		final CustomTaskOutputFile outputFile = new CustomTaskOutputFile();
		outputFile.setDisplayName(displayName);
		outputFile.setFilename(filename);
		outputFile.setContent(content);
		outputFile.setContentType(contentType);
		outputFiles.add(outputFile);
	}

	protected void storeFileOutput(final String displayName, final String filename, final Spreadsheet spreadsheet) {
		try {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			spreadsheet.exportToXLSSheet(outputStream);
			storeFileOutput(displayName, filename, outputStream.toByteArray(), "application/vnd.ms-excel");
		} catch (final IOException e) {
			throw new Error(e);
		}
	}

}
