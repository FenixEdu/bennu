package module.fileSupport.domain;

import java.util.Collection;
import java.util.Collections;

import module.fileSupport.dto.DomainStorageDTO;
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
    public void store(String uniqueIdentification, byte[] content) {
	new FileContent(uniqueIdentification, content);
    }

    @Override
    public byte[] read(String uniqueIdentification) {
	final FileContent fileContent = FileContent.readByUniqueIdentification(uniqueIdentification);
	return fileContent != null ? fileContent.getContent().getBytes() : null;
    }

    @Override
    public Collection<Pair<String, String>> getPresentationDetails() {
	return Collections.EMPTY_LIST;
    }

}
