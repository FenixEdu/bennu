/*
 * @(#)CustomTaskLog.java
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
package myorg.domain.scheduler;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import myorg.domain.MyOrg;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class CustomTaskLog extends CustomTaskLog_Base {

    public static final Comparator<CustomTaskLog> COMPARATOR_BY_UPLOAD_TIME_AND_CLASSNAME = new Comparator<CustomTaskLog>() {

	@Override
	public int compare(final CustomTaskLog o1, final CustomTaskLog o2) {
	    final int d = o2.getUploaded().compareTo(o1.getUploaded());
	    return d == 0 ? compareByName(o1, o2) : d;
	}

	public int compareByName(final CustomTaskLog o1, final CustomTaskLog o2) {
	    final int s = o1.getClassName().compareTo(o2.getClassName());
	    return s == 0 ? o2.hashCode() - o1.hashCode() : s;
	}

    };

    public CustomTaskLog() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public CustomTaskLog(final String className, final String contents, final DateTime uploadTime, final DateTime taskStart,
	    final DateTime taskEnd, final String out, final Set<GenericFile> outputFiles) {
	this();
	setClassName(className);
	setContents(contents);
	setUploaded(uploadTime);
	setTaskStart(taskStart);
	setTaskEnd(taskEnd);
	setOutput(out);
	getGenericFileSet().addAll(outputFiles);
    }

    public static SortedSet<CustomTaskLog> getSortedCustomTaskLogs() {
	final SortedSet<CustomTaskLog> sortedCustomTaskLogs = new TreeSet<CustomTaskLog>(COMPARATOR_BY_UPLOAD_TIME_AND_CLASSNAME);
	sortedCustomTaskLogs.addAll(MyOrg.getInstance().getCustomTaskLogSet());
	return sortedCustomTaskLogs;
    }

    @Service
    public void delete() {
	for (final GenericFile genericFile : getGenericFileSet()) {
	    genericFile.delete();
	}
	setMyOrg(null);
	deleteDomainObject();
    }
}
