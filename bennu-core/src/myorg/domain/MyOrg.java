package myorg.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jvstm.TransactionalCommand;
import pt.ist.fenixWebFramework.Config;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixframework.pstm.DomainClassInfo;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class MyOrg extends MyOrg_Base {
    
    private static MyOrg instance = null;

    public synchronized static void initialize(final Config config) {
	if (instance == null) {
	    Transaction.withTransaction(true, new TransactionalCommand() {
		@Override
		public void doIt() {
		    // Bogus tx to load DomainClassInfo stuff...
		}
	    });
	    final long oid = readOid(config);
	    Transaction.withTransaction(false, new TransactionalCommand() {
		@Override
		public void doIt() {
		    instance = oid == -1 ? new MyOrg() : (MyOrg) Transaction.getObjectForOID(oid);
		}
	    });
	}
    }

    private static long readOid(final Config config) {
	Connection connection = null;
	long oid = 0;
	try {
	    connection = FenixWebFramework.getConnection(config);

	    Statement statementLock = null;
	    ResultSet resultSetLock = null;
	    try {
		statementLock = connection.createStatement();
		resultSetLock = statementLock.executeQuery("SELECT GET_LOCK('MyOrgInit', 100)");
		if (!resultSetLock.next() || (resultSetLock.getInt(1) != 1)) {
		    throw new Error("other.app.has.lock");
		}
	    } finally {
		if (resultSetLock != null) {
		    resultSetLock.close();
		}
		if (statementLock != null) {
		    statementLock.close();
		}
	    }

	    try {
		Statement statementQuery = null;
		ResultSet resultSetQuery = null;
		try {
		    statementQuery = connection.createStatement();
		    resultSetQuery = statementQuery.executeQuery("SELECT ID_INTERNAL FROM MY_ORG");
		    if (resultSetQuery.next()) {
			int idInternal = resultSetQuery.getInt(1);
			int cid = DomainClassInfo.mapClassToId(MyOrg.class);
			oid = ((long) cid << 32) + idInternal;
		    } else {
			oid = -1;
		    }
		} finally {
		    if (resultSetQuery != null) {
			resultSetQuery.close();
		    }
		    if (statementQuery != null) {
			statementQuery.close();
		    }
		}
	    } finally {
		Statement statementUnlock = null;
		try {
		    statementUnlock = connection.createStatement();
		    statementUnlock.executeUpdate("DO RELEASE_LOCK('MyOrgInit')");
		} finally {
		    if (statementUnlock != null) {
			statementUnlock.close();
		    }
		}
	    }

	    connection.commit();
	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    if (connection != null) {
		try {
		    connection.close();
		} catch (SQLException e) {
		    // nothing can be done.
		}
	    }
	}
	return oid;
    }

    public static MyOrg getInstance() {
	if (instance == null) {
	    throw new Error(MyOrg.class.getName() + ".not.initialized");
	}
	return instance;
    }

    private MyOrg() {
	super();
	setApplicationTitle(new MultiLanguageString("MyOrg Application Title"));
	setApplicationSubTitle(new MultiLanguageString("MyOrg Application SubTitle"));
	setApplicationCopyright(new MultiLanguageString("My Organization Name"));
    }

}
