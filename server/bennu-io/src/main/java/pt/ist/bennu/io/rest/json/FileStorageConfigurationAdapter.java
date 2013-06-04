package pt.ist.bennu.io.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.io.domain.FileStorage;
import pt.ist.bennu.io.domain.FileStorageConfiguration;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    public FileStorageConfiguration create(JsonElement el, JsonBuilder ctx) {
        final JsonArray configs = el.getAsJsonArray();
        for (JsonElement configEl : configs) {
            final JsonObject config = configEl.getAsJsonObject();
            final String fileStorageConfigurationId = config.get("fileStorageConfigurationId").getAsString();
            final String fileStorageId = config.get("fileStorageId").getAsString();
            associate(fileStorageConfigurationId, fileStorageId);
        }
        return null;
    }

    private void associate(String fileStorageConfigurationId, String fileStorageId) {
        FileStorageConfiguration fsc = readDomainObject(fileStorageConfigurationId);
        FileStorage fs = readDomainObject(fileStorageId);
        if (fsc != null) {
            fsc.setStorage(fs);
        }
    }

    private <T> T readDomainObject(String oid) {
        if ("null".equals(oid)) {
            return null;
        }
        return FenixFramework.getDomainObject(oid);
    }

    @Override
    public FileStorageConfiguration update(JsonElement arg0, FileStorageConfiguration arg1, JsonBuilder ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement view(FileStorageConfiguration fsc, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", fsc.getExternalId());
        json.addProperty("type", fsc.getFileType());
        json.add("storage", ctx.view(fsc.getStorage()));
        return json;
    }

}
