package module.fileSupport.domain;

import java.util.Collection;

import module.fileSupport.dto.DBStorageDTO;
import module.fileSupport.dto.DomainStorageDTO;
import module.fileSupport.dto.LocalFileSystemStorageDTO;
import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.Pair;

/**
 * 
 * @author Shezad Anavarali Date: Jul 15, bb2009
 * 
 */
abstract public class FileStorage extends FileStorage_Base {

    public FileStorage() {
	super();
	this.setOjbConcreteClass(getClass().getName());
	setMyOrg(MyOrg.getInstance());
    }

    abstract public String store(String uniqueIdentification, byte[] content);

    abstract public byte[] read(String uniqueIdentification);

    @Service
    public static DomainStorage createNew(final DomainStorageDTO storageDTO) {
	return new DomainStorage(storageDTO);
    }

    @Service
    public static LocalFileSystemStorage createNew(LocalFileSystemStorageDTO storageDTO) {
	return new LocalFileSystemStorage(storageDTO);
    }

    @Service
    public static DBStorage createNew(DBStorageDTO storageDTO) {
	return new DBStorage(storageDTO);
    }

    @Service
    public void delete() {
	if (isCanBeDeleted()) {
	    getConfigurations().clear();
	    removeMyOrg();
	    Transaction.deleteObject(this);
	}
    }

    public boolean isCanBeDeleted() {
	return getFilesCount() == 0;
    }

    abstract public Collection<Pair<String, String>> getPresentationDetails();
}
