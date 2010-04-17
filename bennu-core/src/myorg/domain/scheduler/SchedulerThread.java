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
import pt.ist.fenixframework.pstm.Transaction;

public class SchedulerThread extends Thread implements TransactionalCommand {

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(true, this);
    }

    @Override
    public void doIt() {
	for (final Task task : MyOrg.getInstance().getTasksSet()) {
	    for (final TaskConfiguration taskConfiguration : task.getTaskConfigurationsSet()) {
		if (taskConfiguration.shouldRunNow()) {
		    logTaskStart(task);
		    boolean successful = false;
		    try {
			final TaskExecutor taskExecutor = new TaskExecutor(task);
			taskExecutor.start();
			try {
			    taskExecutor.join();
			    successful = true;;
			} catch (InterruptedException e) {
			    throw new Error(e);
			}
		    } finally {
			logTaskEnd(task, successful);
		    }
		}
	    }
	}
    }

    private void logTaskStart(final Task task) {
	final TaskLogger taskLogger = new TaskLogger(task);
	logTask(taskLogger);
    }

    private void logTaskEnd(final Task task, final boolean successful) {
	final TaskLogger taskLogger = new TaskLogger(task, successful);
	logTask(taskLogger);
    }

    private void logTask(final TaskLogger taskLogger) throws Error {
	taskLogger.start();
	try {
	    taskLogger.join();
	} catch (InterruptedException e) {
	    throw new Error(e);
	}
    }

}
