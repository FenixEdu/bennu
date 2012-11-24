package pt.ist.fenixframework.plugins.scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import jvstm.TransactionalCommand;

import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.plugins.scheduler.domain.SchedulerSystem;
import pt.ist.fenixframework.pstm.Transaction;

public class Scheduler extends TimerTask {

    private static final int SCHEDULER_INVOCATION_PERIOD = 120000; // (ms)

    public static void initialize() {
	new Scheduler();
    }

    private final Timer timer = new Timer(true);

    public Scheduler() {
	final DateTime dt = new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(1);
	timer.scheduleAtFixedRate(this, dt.toDate(), SCHEDULER_INVOCATION_PERIOD);
    }

    @Override
    public void run() {
	try {
	    try {
		Transaction.withTransaction(false, new TransactionalCommand() {
		    @Override
		    public void doIt() {
			SchedulerSystem.queueTasks();
		    }
		});
	    } finally {
		Transaction.forceFinish();
	    }
	    runPendingTask();
	} catch (final Throwable t) {
	    t.printStackTrace();
	}
    }

    private void runPendingTask() {
	try {
	    Transaction.withTransaction(true, new TransactionalCommand() {
		@Override
		public void doIt() {
		    final Connection connection = Transaction.getCurrentJdbcConnection();
		    try {
			runPendingTask(connection);
		    } catch (SQLException e) {
			throw new Error(e);
		    }
		}
	    });
	} finally {
	    Transaction.forceFinish();
	}
    }

    private static String getAppDbAliasConnection() {
	final String dbAlias = FenixFramework.getConfig().getDbAlias();
	return dbAlias == null || dbAlias.isEmpty() ?
		"" : dbAlias;
    }

    private void runPendingTask(final Connection connection) throws SQLException {
	Statement statement = null;
	ResultSet resultSet = null;
	try {
	    final String lockVariable = SchedulerSystem.class.getName() + "_" + getAppDbAliasConnection();
	    try {
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT GET_LOCK('" + lockVariable + "', 10)");
		if (resultSet.next() && (resultSet.getInt(1) == 1)) {
		    SchedulerSystem.runPendingTask();
		}
	    } finally {
		Statement statement2 = null;
		try {
		    statement2 = connection.createStatement();
		    statement2.executeUpdate("DO RELEASE_LOCK('" + lockVariable + "')");
		} finally {
		    if (statement2 != null) {
			try {
			    statement2.close();
			} catch (final SQLException e) {
//			    e.printStackTrace();
			}
		    }
		}
	    }
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
//		    e.printStackTrace();
		}
	    }
	    if (statement != null) {
		try {
		    statement.close();
		} catch (SQLException e) {
//		    e.printStackTrace();
		}
	    }
	}
    }

}
