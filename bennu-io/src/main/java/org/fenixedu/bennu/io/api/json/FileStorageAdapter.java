package org.fenixedu.bennu.io.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.io.domain.FileStorage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(FileStorage.class)
public class FileStorageAdapter implements JsonViewer<FileStorage> {

    @Override
    public JsonElement view(final FileStorage fs, final JsonBuilder arg1) {
        final JsonObject json = new JsonObject();
        json.addProperty("id", fs.getExternalId());
        json.addProperty("name", fs.getName());
        json.addProperty("type", fs.getClass().getSimpleName());
        json.addProperty("default", fs.isDefault());
        return json;
    }

}
