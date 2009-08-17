package module.fileSupport.domain;

import java.util.Collection;
import java.util.Collections;

import module.fileSupport.dto.DomainStorageDTO;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.utl.ist.fenix.tools.util.Pair;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public class DomainStorage extends DomainStorage_Base {

    public DomainStorage() {
	super();
    }

    public DomainStorage(DomainStorageDTO storageDTO) {
	this();
	setName(storageDTO.getName());
    }

    @Override
    public String store(String uniqueIdentification, byte[] content) {
	final DomainObject existingRawData = AbstractDomainObject.fromExternalId(uniqueIdentification);
	if (existingRawData instanceof FileRawData && existingRawData != null) {
	    ((FileRawData) existingRawData).delete();
	}

	if (content != null) {
	    return new FileRawData(uniqueIdentification, content).getExternalId();
	}
	return null;
    }

    @Override
    public byte[] read(String uniqueIdentification) {
	final FileRawData rawData = FileRawData.fromExternalId(uniqueIdentification);
	return rawData != null ? rawData.getContent().getBytes() : null;
    }

    @Override
    public Collection<Pair<String, String>> getPresentationDetails() {
	return Collections.EMPTY_LIST;
    }

}
