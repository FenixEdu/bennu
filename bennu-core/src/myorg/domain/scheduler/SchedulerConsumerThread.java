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

import java.util.ArrayList;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;

public class SchedulerConsumerThread extends Thread implements TransactionalCommand {

    public class TaskRepeatQueue extends ArrayList<Task> {
	private static final long serialVersionUID = 1L;

	public void offer(Task task) {
	    add(0, task);
	}

	public Task poll() {
	    int lastPosition = size() - 1;
	    Task lastTask = get(lastPosition);
	    remove(lastPosition);
	    return lastTask;
	}
    }

    private final TaskRepeatQueue tasksToRepeat = new TaskRepeatQueue();

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(false, this);
    }

    @Override
    public void doIt() {
	final PendingExecutionTaskQueue pendingTasksQueue = PendingExecutionTaskQueue.getPendingExecutionTaskQueue();
	while (!pendingTasksQueue.isEmpty()) {
	    Task task = pendingTasksQueue.poll();
	    runTask(task);
	}

	while (!tasksToRepeat.isEmpty()) {
	    PendingExecutionTaskQueue.getPendingExecutionTaskQueue().offer(tasksToRepeat.poll());
	}
    }

    private void runTask(final Task task) {
	logTaskStart(task);
	boolean successful = false;
	try {
	    final TaskExecutor taskExecutor = new TaskExecutor(task);
	    taskExecutor.start();
	    try {
		taskExecutor.join();
		successful = taskExecutor.isSuccessful();
	    } catch (InterruptedException e) {
	    }
	} finally {
	    logTaskEnd(task, successful);
	    if ((!successful) && task.isRepeatedOnFailure()) {
		tasksToRepeat.offer(task);
	    }
	}
    }

    private void logTaskStart(final Task task) {
	final TaskLogger taskLogger = new TaskLogger(task);
	taskLogger.run();
    }

    private void logTaskEnd(final Task task, final boolean successful) {
	final TaskLogger taskLogger = new TaskLogger(task, successful);
	taskLogger.run();
    }

}
