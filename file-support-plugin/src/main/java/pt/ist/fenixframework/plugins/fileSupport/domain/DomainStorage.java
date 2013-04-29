package pt.ist.fenixframework.plugins.fileSupport.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public class DomainStorage extends DomainStorage_Base {

    public DomainStorage() {
        super();
    }

    public DomainStorage(String name) {
        this();
        setName(name);
    }

    @Override
    public String store(String uniqueIdentification, byte[] content) {
        final DomainObject existingRawData = FenixFramework.getDomainObject(uniqueIdentification);
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
        final FileRawData rawData = FenixFramework.getDomainObject(uniqueIdentification);
        return rawData != null ? rawData.getContent().getBytes() : null;
    }

    @Override
    public InputStream readAsInputStream(String uniqueIdentification) {
        return new ByteArrayInputStream(read(uniqueIdentification));
    }

    // @Override
    // public Collection<Pair<String, String>> getPresentationDetails() {
    // return Collections.EMPTY_LIST;
    // }
}