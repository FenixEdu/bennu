/*
 * @(#)TaskLog.java
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

package myorg.domain.scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import myorg.domain.MyOrg;

import org.joda.time.DateTime;

public class TaskLog extends TaskLog_Base {

    public static final Comparator<TaskLog> COMPARATOR_BY_ID = new Comparator<TaskLog>() {

	@Override
	public int compare(final TaskLog taskLog1, final TaskLog taskLog2) {
	    return taskLog1.getExternalId().compareTo(taskLog2.getExternalId());
	}

    };

    public static final Comparator<TaskLog> COMPARATOR_BY_START = new Comparator<TaskLog>() {

	@Override
	public int compare(final TaskLog taskLog1, final TaskLog taskLog2) {
	    int c = 0;
	    if ((taskLog1.getTaskStart() != null) && (taskLog2.getTaskStart() != null)) {
		c = taskLog1.getTaskStart().compareTo(taskLog2.getTaskStart());
	    } else if (taskLog1.getTaskStart() != null) {
		c = -1;
	    } else if (taskLog2.getTaskStart() != null) {
		c = 1;
	    }
	    return c != 0 ? c : COMPARATOR_BY_ID.compare(taskLog1, taskLog2);
	}

    };

    public static final Comparator<TaskLog> COMPARATOR_BY_END = new Comparator<TaskLog>() {

	@Override
	public int compare(final TaskLog taskLog1, final TaskLog taskLog2) {
	    int c = 0;
	    if ((taskLog1.getTaskEnd() != null) && (taskLog2.getTaskEnd() != null)) {
		c = taskLog1.getTaskEnd().compareTo(taskLog2.getTaskEnd());
	    } else if (taskLog1.getTaskEnd() != null) {
		c = -1;
	    } else if (taskLog2.getTaskEnd() != null) {
		c = 1;
	    }
	    return c != 0 ? c : COMPARATOR_BY_START.compare(taskLog1, taskLog2);
	}

    };

    public TaskLog(final Task task) {
	super();
	setMyOrg(MyOrg.getInstance());
	setTask(task);
	setSuccessful(Boolean.FALSE);
	setTaskStart(new DateTime());
    }

    public void update(final Boolean successful, String output) {
	setTaskEnd(new DateTime());
	setOutput(output);
	setSuccessful(successful);
    }

    @Override
    public Boolean getSuccessful() {
	return getTaskEnd() == null ? null : super.getSuccessful();
    }

    public Collection<String> getOutputLines() {
	return Arrays.asList(getOutput() != null ? getOutput().split("\n") : new String[0]);
    }
}
