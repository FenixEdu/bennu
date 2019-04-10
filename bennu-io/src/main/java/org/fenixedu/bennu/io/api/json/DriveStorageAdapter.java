package org.fenixedu.bennu.io.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.io.domain.DriveStorage;
import org.fenixedu.bennu.io.domain.FileStorage;

@DefaultJsonAdapter(DriveStorage.class)
public class DriveStorageAdapter implements JsonAdapter<DriveStorage> {

    @Override
    public DriveStorage create(JsonElement el, JsonBuilder ctx) {
        final JsonObject obj = el.getAsJsonObject();
        final String name = obj.get("name").getAsString();
        final String driveUrl = obj.get("driveUrl").getAsString();
        final String appId = obj.get("appId").getAsString();
        final String appUser = obj.get("appUser").getAsString();
        final String refreshToken = obj.get("refreshToken").getAsString();
        final String storeDirSlug = obj.get("storeDirSlug").getAsString();
        final int fileIdLength = obj.get("fileIdLength").getAsInt();
        final int folderNameMaxLength = obj.get("folderNameMaxLength").getAsInt();
        return FileStorage.createNewDriveStorage(name, driveUrl, appId, appUser, refreshToken,storeDirSlug, fileIdLength, folderNameMaxLength);
    }

    @Override
    public DriveStorage update(JsonElement arg0, DriveStorage arg1, JsonBuilder arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement view(DriveStorage fls, JsonBuilder ctx) {
        final JsonObject json = ctx.view(fls, FileStorageAdapter.class).getAsJsonObject();
        json.addProperty("driveUrl", fls.getDriveUrl());
        json.addProperty("appId", fls.getAppId());
        json.addProperty("appUser", fls.getAppUser());
        json.addProperty("refreshToken", fls.getRefreshToken());
        json.addProperty("storeDirSlug", fls.getStoreDirectorySlug());
        json.addProperty("fileIdLength", fls.getFileIdLength());
        json.addProperty("folderNameMaxLength", fls.getFolderNameMaxLength());
        return json;
    }
}
