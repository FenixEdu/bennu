/**
 * 
 */
package module.fileSupport.dto;

/**
 * @author Shezad Anavarali Date: Jul 20, 2009
 * 
 */
public class DBStorageDTO extends FileStorageDTO {

    String host;

    Integer port;

    String dbName;

    String tableName;

    String username;

    String password;

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public Integer getPort() {
	return port;
    }

    public void setPort(Integer port) {
	this.port = port;
    }

    public String getDbName() {
	return dbName;
    }

    public void setDbName(String dbName) {
	this.dbName = dbName;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getTableName() {
	return tableName;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

}
