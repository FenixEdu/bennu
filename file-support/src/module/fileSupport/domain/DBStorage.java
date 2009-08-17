package module.fileSupport.domain;

import java.util.Collection;
import java.util.Collections;

import pt.utl.ist.fenix.tools.util.Pair;
import module.fileSupport.dto.DBStorageDTO;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public class DBStorage extends DBStorage_Base {

    public DBStorage() {
	super();
    }

    public DBStorage(DBStorageDTO storageDTO) {
	this();
	setName(storageDTO.getName());
	setHost(storageDTO.getHost());
	setPort(storageDTO.getPort());
	setDbName(storageDTO.getDbName());
	setTableName(storageDTO.getTableName());
	setUsername(storageDTO.getUsername());
	setPassword(storageDTO.getPassword());
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
    public Collection<Pair<String, String>> getPresentationDetails() {
	// TODO Auto-generated method stub
	return Collections.EMPTY_LIST;
    }

}
