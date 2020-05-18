package org.fenixedu.bennu.io.api.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.io.domain.FileStorageConfiguration;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@DefaultJsonAdapter(FileStorageConfiguration.class)
public class FileStorageConfigurationAdapter implements JsonAdapter<FileStorageConfiguration> {

    /***
     * [ {'fileStorageConfigurationId' : 'x1', 'fileStorageId' : 'y1'} ,
     * {'fileStorageConfigurationId' : 'x2', 'fileStorageId' : 'y2'} ,
     * ...
     * ]
     */
    @Override
    @Atomic
    public FileStorageConfiguration create(final JsonElement el, final JsonBuilder ctx) {
        final JsonArray configs = el.getAsJsonArray();
        for (final JsonElement configEl : configs) {
            final JsonObject config = configEl.getAsJsonObject();
            final String fileStorageConfigurationId = config.get("fileStorageConfigurationId").getAsString();
            final String fileStorageId = config.get("fileStorageId").getAsString();
            associate(fileStorageConfigurationId, fileStorageId);
        }
        return null;
    }

    private void associate(final String fileStorageConfigurationId, final String fileStorageId) {
        final FileStorageConfiguration fsc = readDomainObject(fileStorageConfigurationId);
        final FileStorage fs = readDomainObject(fileStorageId);
        if (fsc != null) {
            fsc.setStorage(fs);
        }
    }

    private <T> T readDomainObject(final String oid) {
        return "null".equals(oid) ? null : FenixFramework.getDomainObject(oid);
    }

    @Override
    public FileStorageConfiguration update(final JsonElement arg0, final FileStorageConfiguration arg1,
                                           final JsonBuilder ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement view(final FileStorageConfiguration fsc, final JsonBuilder ctx) {
        final JsonObject json = new JsonObject();
        json.addProperty("id", fsc.getExternalId());
        json.addProperty("type", fsc.getFileType());
        json.add("storage", ctx.view(fsc.getStorage()));
        return json;
    }

}
