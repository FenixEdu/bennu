package pt.ist.bennu.io.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.io.domain.FileStorage;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(FileStorage.class)
public class FileStorageAdapter implements JsonAdapter<FileStorage> {

    @Override
    public FileStorage create(JsonElement arg0, JsonBuilder arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileStorage update(JsonElement arg0, FileStorage arg1, JsonBuilder arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement view(FileStorage fs, JsonBuilder arg1) {
        final JsonObject json = new JsonObject();
        json.addProperty("id", fs.getExternalId());
        json.addProperty("name", fs.getName());
        json.addProperty("type", fs.getClass().getSimpleName());
        json.addProperty("filesCount", fs.getFilesSet().size());
        return json;
    }
}
