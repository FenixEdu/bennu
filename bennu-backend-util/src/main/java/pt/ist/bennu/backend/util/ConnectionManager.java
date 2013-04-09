package pt.ist.bennu.backend.util;

import java.sql.Connection;

import org.apache.ojb.broker.accesslayer.LookupException;

import pt.ist.fenixframework.backend.jvstmojb.pstm.TransactionSupport;

public class ConnectionManager {

    public static Connection getCurrentSQLConnection() {
        try {
            return TransactionSupport.getOJBBroker().serviceConnectionManager().getConnection();
        } catch (LookupException e) {
            throw new Error(e);
        }
    }

}
