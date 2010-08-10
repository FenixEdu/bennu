/*
 * @(#)Scheduler.java
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;

public class Scheduler extends TimerTask {

    private static final String LOCK_VARIABLE = Scheduler.class.getName();

    public static void initialize() {
	try {
	    Task.initTasks();
	    new Scheduler();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new Error(e);
	} catch (InstantiationException e) {
	    e.printStackTrace();
	    throw new Error(e);
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new Error(e);
	}
    }

    private final Timer timer = new Timer(true);

    public Scheduler() {
	timer.scheduleAtFixedRate(this, 10000, 20000);
    }

    @Override
    public void run() {
	try {
	    Transaction.withTransaction(true, new TransactionalCommand() {
		@Override
		public void doIt() {
		    final Connection connection = Transaction.getCurrentJdbcConnection();
		    try {
			run(connection);
		    } catch (SQLException e) {
			throw new Error(e);
		    }
		}
	    });
	} finally {
	    Transaction.forceFinish();
	}
    }

    private void run(final Connection connection) throws SQLException {
	Statement statement = null;
	ResultSet resultSet = null;
	try {
	    try {
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT GET_LOCK('" + LOCK_VARIABLE + "', 10)");
		if (resultSet.next() && (resultSet.getInt(1) == 1)) {
		    runTasks();
		}
	    } finally {
		Statement statement2 = null;
		try {
		    statement2 = connection.createStatement();
		    statement2.executeUpdate("DO RELEASE_LOCK('" + LOCK_VARIABLE + "')");
		} finally {
		    if (statement2 != null) {
			try {
			    statement2.close();
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	    if (statement != null) {
		try {
		    statement.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private void runTasks() {
	final SchedulerProducerThread schedulerProducerThread = new SchedulerProducerThread();
	schedulerProducerThread.start();
	try {
	    schedulerProducerThread.join();
	    final SchedulerConsumerThread schedulerConsumerThread = new SchedulerConsumerThread();
	    schedulerConsumerThread.start();
	    schedulerConsumerThread.join();
	} catch (InterruptedException e) {
	    throw new Error(e);
	}
    }

}
