/*
 * @(#)CustomTaskLogAggregate.java
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
package myorg.presentationTier;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import myorg.domain.MyOrg;
import myorg.domain.scheduler.CustomTaskLog;

import org.joda.time.DateTime;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class CustomTaskLogAggregate implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Comparator<CustomTaskLogAggregate> COMPARATOR_BY_LAST_UPLOAD_DATE_AND_CLASS_NAME = new Comparator<CustomTaskLogAggregate>() {

	@Override
	public int compare(final CustomTaskLogAggregate o1, final CustomTaskLogAggregate o2) {
	    final int d = o2.getLastUploadDate().compareTo(o1.getLastUploadDate());
	    return d == 0 ? compareByClassName(o1, o2) : d;
	}

	public int compareByClassName(final CustomTaskLogAggregate o1, final CustomTaskLogAggregate o2) {
	    final int d = o1.getClassName().compareTo(o2.getClassName());
	    return d == 0 ? o2.hashCode() - o1.hashCode() : d;
	}
    };

    private SortedSet<CustomTaskLog> customTaskLogs;

    private String className;

    private DateTime lastUploadDate;

    public CustomTaskLogAggregate(String className) {
	setClassName(className);
	setCustomTaskLogs(new TreeSet<CustomTaskLog>(CustomTaskLog.COMPARATOR_BY_UPLOAD_TIME_AND_CLASSNAME));
	searchCustomTaskLogs(className);
    }

    public void setCustomTaskLogs(SortedSet<CustomTaskLog> customTaskLogs) {
	this.customTaskLogs = customTaskLogs;
    }

    public SortedSet<CustomTaskLog> getCustomTaskLogs() {
	return customTaskLogs;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public String getClassName() {
	return className;
    }

    public void setLastUploadDate(DateTime lastUploadDate) {
	this.lastUploadDate = lastUploadDate;
    }

    public DateTime getLastUploadDate() {
	return lastUploadDate;
    }

    public int getSize() {
	return customTaskLogs.size();
    }

    public void searchCustomTaskLogs(String searchClassName) {
	for (CustomTaskLog taskLog : MyOrg.getInstance().getCustomTaskLogSet()) {
	    String taskClassName = taskLog.getClassName();
	    if (taskClassName == null) {
		taskClassName = "";
	    }

	    if (taskClassName.equals(searchClassName)) {
		getCustomTaskLogs().add(taskLog);
		if ((getLastUploadDate() == null) || (getLastUploadDate().isBefore(taskLog.getUploaded()))) {
		    setLastUploadDate(taskLog.getUploaded());
		}
	    }
	}
    }

    public void deleteCustomTaskLogs() {
	for (CustomTaskLog taskLog : getCustomTaskLogs()) {
	    taskLog.delete();
	}
	getCustomTaskLogs().clear();
    }
}
