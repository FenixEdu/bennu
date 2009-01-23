package myorg.persistenceTier.externalRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import myorg._development.PropertiesManager;

public class DbHandler {

    private static final ThreadLocal<Map<String, DbHandler>> handlerMap = new ThreadLocal<Map<String,DbHandler>>();

    public static DbHandler getDbHandler(final String dbPropertyPrefix) {
	Map<String, DbHandler> map = handlerMap.get();
	if (map == null) {
	    map = new HashMap<String, DbHandler>();
	    handlerMap.set(map);
	}
	final DbHandler dbHandler;
	if (map.containsKey(dbPropertyPrefix)) {
	    dbHandler = map.get(dbPropertyPrefix);
	} else {
	    dbHandler = new DbHandler(dbPropertyPrefix);
	    map.put(dbPropertyPrefix, dbHandler);
	}
	return dbHandler;
    }

    public static void commitAll() {
	final Map<String, DbHandler> map = handlerMap.get();
	if (map != null) {
	    handlerMap.set(null);
	    for (final DbHandler dbHandler : map.values()) {
		try {
		    dbHandler.commit();
		} finally {
		    dbHandler.closeConnection();
		}
	    }
	}
    }

    public static void rolebackAll() {
	final Map<String, DbHandler> map = handlerMap.get();
	if (map != null) {
	    handlerMap.set(null);
	    for (final DbHandler dbHandler : map.values()) {
		try {
		    dbHandler.rollback();
		} finally {
		    dbHandler.closeConnection();
		}
	    }
	}
    }

    private Connection connection = null;

    public DbHandler(final String dbPropertyPrefix) {
	try {
	    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	    connection = DriverManager.getConnection(getDatabaseUrl(dbPropertyPrefix));
	    connection.setAutoCommit(false);
	} catch (final SQLException exception) {
	    throw new Error(exception);
	}
    }

    protected String getDatabaseUrl(final String dbPropertyPrefix) {
	StringBuilder stringBuffer = new StringBuilder();
	stringBuffer.append("jdbc:oracle:thin:");
	stringBuffer.append(PropertiesManager.getProperty(dbPropertyPrefix + ".user"));
	stringBuffer.append("/");
	stringBuffer.append(PropertiesManager.getProperty(dbPropertyPrefix + ".pass"));
	stringBuffer.append("@");
	stringBuffer.append(PropertiesManager.getProperty(dbPropertyPrefix + ".alias"));
	return stringBuffer.toString();
    }

    Connection getConnection() {
        return connection;
    }

    protected void commit() {
	if (connection != null) {
	    try {
		connection.commit();
	    } catch (final SQLException e) {
		throw new Error(e);
	    }
	}	
    }

    protected void rollback() {
	if (connection != null) {
	    try {
		connection.rollback();
	    } catch (final SQLException e) {
		throw new Error(e);
	    }
	}	
    }

    protected void closeConnection() {
	if (connection != null) {
	    try {
		connection.close();
	    } catch (final SQLException e) {
		e.printStackTrace();
	    }
	}	
    }

}
