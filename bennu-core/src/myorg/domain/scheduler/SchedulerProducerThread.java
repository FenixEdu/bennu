/*
 * @(#)SchedulerThread.java
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

import jvstm.TransactionalCommand;
import myorg.domain.MyOrg;

import org.joda.time.DateTime;

import pt.ist.fenixframework.pstm.Transaction;

public class SchedulerProducerThread extends Thread implements TransactionalCommand {

    private DateTime startTime;

    public void setStartTime(DateTime startTime) {
	this.startTime = startTime;
    }

    public DateTime getStartTime() {
	return startTime;
    }

    @Override
    public void start() {
	setStartTime(new DateTime());
	super.start();
    }

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(false, this);
    }

    @Override
    public void doIt() {
	PendingExecutionTaskQueue queue = PendingExecutionTaskQueue.getPendingExecutionTaskQueue();
	for (final Task task : MyOrg.getInstance().getTasksSet()) {
	    if (queue.contains(task)) {
		continue;
	    }
	    for (final TaskConfiguration taskConfiguration : task.getTaskConfigurationsSet()) {
		if (taskConfiguration.shouldRunNow(getStartTime())) {
		    queue.offer(task);
		    break;
		}
	    }
	}
    }
}
