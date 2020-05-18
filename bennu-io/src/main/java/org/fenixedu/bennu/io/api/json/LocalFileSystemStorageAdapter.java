package org.fenixedu.bennu.io.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.io.domain.LocalFileSystemStorage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(LocalFileSystemStorage.class)
public class LocalFileSystemStorageAdapter implements JsonAdapter<LocalFileSystemStorage> {

    @Override
    public LocalFileSystemStorage create(final JsonElement el, final JsonBuilder ctx) {
        final JsonObject obj = el.getAsJsonObject();
        final String name = obj.get("name").getAsString();
        final String path = obj.get("path").getAsString();
        final Integer treeDirectoriesNameLength = obj.get("treeDirectoriesNameLength").getAsInt();
        return FileStorage.createNewFileSystemStorage(name, path, treeDirectoriesNameLength);
    }

    @Override
    public LocalFileSystemStorage update(final JsonElement arg0, final LocalFileSystemStorage arg1,
                                         final JsonBuilder arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement view(final LocalFileSystemStorage fls, final JsonBuilder ctx) {
        final JsonObject json = ctx.view(fls, FileStorageAdapter.class).getAsJsonObject();
        json.addProperty("path", fls.getPath());
        json.addProperty("treeDirectoriesNameLength", fls.getTreeDirectoriesNameLength());
        return json;
    }

}
