package pt.ist.bennu.backend.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.backend.jvstmojb.JvstmOJBConfig;

public class LockManager {

    private final static String ALIAS = FenixFramework.<JvstmOJBConfig> getConfig().getDbAlias();

    @SuppressWarnings("resource")
    public static boolean acquireDistributedLock(String lockName) {
        try {
            Connection connection = ConnectionManager.getCurrentSQLConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT GET_LOCK('" + lockName + ALIAS + "', 10)");

            boolean result = resultSet.next() && (resultSet.getInt(1) == 1);
            resultSet.close();
            statement.close();

            return result;
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    @SuppressWarnings("resource")
    public static void releaseDistributedLock(String lockName) {
        try {
            Connection connection = ConnectionManager.getCurrentSQLConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DO RELEASE_LOCK('" + lockName + ALIAS + "')");

            statement.close();
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

}
