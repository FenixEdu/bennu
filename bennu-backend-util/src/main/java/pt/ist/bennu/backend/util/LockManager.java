package pt.ist.bennu.backend.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ojb.broker.accesslayer.LookupException;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.backend.jvstmojb.JvstmOJBConfig;
import pt.ist.fenixframework.backend.jvstmojb.pstm.TransactionSupport;

public class LockManager {

    private final static String ALIAS = FenixFramework.<JvstmOJBConfig> getConfig().getDbAlias();

    @SuppressWarnings("resource")
    public static boolean acquireDistributedLock(String lockName) {
        try {
            Connection connection = getConnection();
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
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DO RELEASE_LOCK('" + lockName + ALIAS + "')");

            statement.close();
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    private static Connection getConnection() {
        try {
            return TransactionSupport.getOJBBroker().serviceConnectionManager().getConnection();
        } catch (LookupException e) {
            throw new Error(e);
        }
    }

}
