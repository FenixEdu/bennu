package myorg.domain.scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;

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
	final SchedulerThread schedulerThread = new SchedulerThread();
	schedulerThread.start();
	try {
	    schedulerThread.join();
	} catch (InterruptedException e) {
	    throw new Error(e);
	}
    }

}
