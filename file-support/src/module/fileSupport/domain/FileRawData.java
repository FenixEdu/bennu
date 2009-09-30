package module.fileSupport.domain;

import myorg.domain.MyOrg;
import myorg.domain.util.ByteArray;

/**
 * 
 * @author Shezad Anavarali Date: Aug 11, 2009
 * 
 */
public class FileRawData extends FileRawData_Base {

    public FileRawData() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public FileRawData(String uniqueIdentification, byte[] content) {
	this();
	setContent(new ByteArray(content));
	setContentKey(uniqueIdentification);
    }

    public void delete() {
	removeMyOrg();
	deleteDomainObject();
    }
}
