/*
 * @(#)Scheduler.java
 *
 * Copyright 2012 Instituto Superior Tecnico
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import jvstm.TransactionalCommand;
import pt.ist.bennu.core._development.PropertiesManager;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class Scheduler extends TimerTask {

    private static final String LOCK_VARIABLE = Scheduler.class.getName() + "_" + getAppDbAliasConnection();

    private static volatile Scheduler instance = null;

    // Public API starts here

    /*
     * The method is synchronized so no two threads will create instances of
     * Scheduler
     */
    public synchronized static void initialize() {
	if (instance != null)
	    return;

	final String scheduleSystemFlag = PropertiesManager.getProperty("schedule.system");
	if (scheduleSystemFlag == null || scheduleSystemFlag.isEmpty() || !scheduleSystemFlag.equalsIgnoreCase("active")) {
	    // SchedulerSystem.getInstance().clearAllScheduledTasks();
	} else {
	    pt.ist.fenixframework.plugins.scheduler.Scheduler.initialize();
	}

	try {
	    Task.initTasks();
	    instance = new Scheduler();
	} catch (final ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new Error(e);
	} catch (final InstantiationException e) {
	    e.printStackTrace();
	    throw new Error(e);
	} catch (final IllegalAccessException e) {
	    e.printStackTrace();
	    throw new Error(e);
	}
    }

    public static boolean isInitialized() {
	return instance != null;
    }

    public static void shutdown() {
	instance.timer.cancel();
    }

    // Internals

    private static String getAppDbAliasConnection() {
	final String dbAlias = PropertiesManager.getProperty("db.alias");
	return dbAlias == null || dbAlias.isEmpty() ?
		"" : dbAlias;
    }

    private final Timer timer = new Timer(true);

    private Scheduler() {
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
			} catch (final SQLException e) {
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
	} catch (final InterruptedException e) {
	    e.printStackTrace();
	    throw new Error(e);
	}
    }

}
