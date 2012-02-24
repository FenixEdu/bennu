package pt.ist.fenixframework.plugins.fileSupport.domain;

import java.io.InputStream;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public class DBStorage extends DBStorage_Base {

    public DBStorage() {
	super();
    }

    public DBStorage(String name, String host, Integer port, String dbName, String tableName, String username, String password) {
	this();
	setName(name);
	setHost(host);
	setPort(port);
	setDbName(dbName);
	setTableName(tableName);
	setUsername(username);
	setPassword(password);
    }

    @Override
    public String store(String uniqueIdentification, byte[] content) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public byte[] read(String uniqueIdentification) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public InputStream readAsInputStream(String uniqueIdentification) {
	// TODO Auto-generated method stub
	return null;
    }

    // @Override
    // public Collection<Pair<String, String>> getPresentationDetails() {
    // // TODO Auto-generated method stub
    // return Collections.EMPTY_LIST;
    // }

}
