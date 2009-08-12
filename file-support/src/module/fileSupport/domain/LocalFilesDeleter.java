package module.fileSupport.domain;

import java.util.ArrayList;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author Shezad Anavarali Date: Aug 12, 2009
 * 
 */
public class LocalFilesDeleter extends LocalFilesDeleter_Base {

    public LocalFilesDeleter() {
	super();
    }

    @Service
    @Override
    public void executeTask() {
	for (final LocalFileToDelete localFileToDelete : new ArrayList<LocalFileToDelete>(MyOrg.getInstance()
		.getLocalFilesToDelete())) {
	    localFileToDelete.delete();
	}
    }

    @Override
    public String getLocalizedName() {
	return getClass().getName();
    }

}
