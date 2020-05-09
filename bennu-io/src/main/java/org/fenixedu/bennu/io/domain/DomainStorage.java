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

    DomainStorage(final String name) {
        setName(name);
    }

    @Override
    public String store(final GenericFile file, final byte[] content) {
        final String uniqueIdentification = file.getContentKey();
        final DomainObject existingRawData = FenixFramework.getDomainObject(uniqueIdentification);
        if (existingRawData != null && existingRawData instanceof FileRawData) {
            ((FileRawData) existingRawData).delete();
        }
        return content == null ? null : new FileRawData(uniqueIdentification == null ?
                file.getExternalId() : uniqueIdentification, content).getExternalId();
    }

    @Override
    public byte[] read(final GenericFile file) {
        final FileRawData rawData = FenixFramework.getDomainObject(file.getContentKey());
        return rawData != null ? rawData.getContent() : null;
    }

    @Override
    public InputStream readAsInputStream(final GenericFile file) {
        final byte[] read = read(file);
        return read != null ? new ByteArrayInputStream(read) : null;
    }

}