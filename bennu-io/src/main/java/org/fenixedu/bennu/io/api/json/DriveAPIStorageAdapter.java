package org.fenixedu.bennu.io.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.io.domain.DriveAPIStorage;
import org.fenixedu.bennu.io.domain.FileStorage;

@DefaultJsonAdapter(DriveAPIStorage.class)
public class DriveAPIStorageAdapter implements JsonAdapter<DriveAPIStorage> {

    @Override
    public DriveAPIStorage create(final JsonElement el, final JsonBuilder ctx) {
        final JsonObject obj = el.getAsJsonObject();
        final String name = obj.get("name").getAsString();
        final String driveUrl = obj.get("driveUrl").getAsString();
        final String remoteUsername = obj.get("remoteUsername").getAsString();
        final String remoteDirectoryId = obj.get("remoteDirectoryId").getAsString();
        return FileStorage.createNewDriveAPIStorage(name, driveUrl, remoteUsername, remoteDirectoryId);
    }

    @Override
    public DriveAPIStorage update(final JsonElement arg0, final DriveAPIStorage arg1,
                                         final JsonBuilder arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement view(final DriveAPIStorage fls, final JsonBuilder ctx) {
        final JsonObject json = ctx.view(fls, FileStorageAdapter.class).getAsJsonObject();
        json.addProperty("driveUrl", fls.getDriveUrl());
        json.addProperty("remoteUsername", fls.getRemoteUsername());
        json.addProperty("remoteDirectoryId", fls.getRemoteDirectoryId());
        return json;
    }

}

