package org.fenixedu.bennu.io.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public final class DomainStorage extends DomainStorage_Base {
    DomainStorage(String name) {
        super();
        setName(name);
    }

    @Override
    public String store(GenericFile file, byte[] content) {
        String uniqueIdentification = file.getContentKey();
        final DomainObject existingRawData = FenixFramework.getDomainObject(uniqueIdentification);
        if (existingRawData != null && existingRawData instanceof FileRawData) {
            ((FileRawData) existingRawData).delete();
        }

        if (content != null) {
            return new FileRawData(uniqueIdentification == null ? file.getExternalId() : uniqueIdentification, content)
                    .getExternalId();
        }
        return null;
    }

    @Override
    public byte[] read(GenericFile file) {
        final FileRawData rawData = FenixFramework.getDomainObject(file.getContentKey());
        return rawData != null ? rawData.getContent() : null;
    }

    @Override
    public InputStream readAsInputStream(GenericFile file) {
        byte[] read = read(file);
        return read != null ? new ByteArrayInputStream(read) : null;
    }
}