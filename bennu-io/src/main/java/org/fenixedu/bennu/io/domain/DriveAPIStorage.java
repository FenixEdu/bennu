package org.fenixedu.bennu.io.domain;

import com.google.common.hash.Funnels;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.SignatureAlgorithm;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.jwt.Tools;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class DriveAPIStorage extends DriveAPIStorage_Base {

    private static final Logger logger = LoggerFactory.getLogger(DriveAPIStorage.class.getName());

    static {
        Unirest.config().reset();
        Unirest.config().followRedirects(false);
        Unirest.config().concurrency(2000, 2000);
    }

    DriveAPIStorage(final String name, final String driveUrl, final String remoteUsername,
                    final String remoteDirectoryId) {
        setName(name);
        setDriveUrl(driveUrl);
        setRemoteUsername(remoteUsername);
        setRemoteDirectoryId(remoteDirectoryId);
    }

    private transient String accessToken = null;
    private transient long accessTokenValidUnit = System.currentTimeMillis() - 1;

    private String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() >= accessTokenValidUnit) {
            synchronized (this) {
                if (accessToken == null || System.currentTimeMillis() >= accessTokenValidUnit) {
                    final JsonObject claim = new JsonObject();
                    claim.addProperty("username", getRemoteUsername());
                    accessToken = Tools.sign(SignatureAlgorithm.RS256, CoreConfiguration.getConfiguration().jwtPrivateKeyPath(), claim);
                }
            }
        }
        return accessToken;
    }

    private String uploadFile(final String directory, final Function<MultipartBody, MultipartBody> fileSetter) {
        final MultipartBody request = Unirest.post(getDriveUrl() + "/api/drive/directory/" + getRemoteDirectoryId())
                .header("Authorization", "Bearer " + getAccessToken())
                .header("X-Requested-With", "XMLHttpRequest")
                .field("path", directory);
        final HttpResponse<String> response = fileSetter.apply(request).asString();
        final JsonObject result = new JsonParser().parse(response.getBody()).getAsJsonObject();
        final JsonElement id = result.get("id");
        if (id == null || id.isJsonNull()) {
            throw new Error(result.toString());
        }
        return id.getAsString();
    }

    private boolean deleteFile(final String fileId) {
        final HttpResponse response = Unirest.delete(getDriveUrl() + "/api/drive/file/" + fileId)
                .header("Authorization", "Bearer " + getAccessToken())
                .header("X-Requested-With", "XMLHttpRequest")
                .asEmpty();
        try {
            if (response.getStatus() != 204) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to delete file: " + fileId + ". Got response code: " + response.getStatus());
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed message:" + response.getBody());
                }
            }
            return true;
        } finally {
            response.getBody();
        }
    }

    private static String dirnameFor(final GenericFile file) {
        final DateTime when = file.getCreationDate();
        return Integer.toString(when.getYear()) + "/" + when.getMonthOfYear() + "/" + when.getDayOfMonth() + "/"
                + file.getExternalId();
    }

    @Override
    public String store(final GenericFile file, final byte[] content) {
        if (content == null) {
            if (!deleteFile(file.getContentKey())) {
                throw new Error("Unable to delete remote file " + file.getExternalId());
            }
            return null;
        }
        return uploadFile(dirnameFor(file), b -> b.field("file", content, file.getFilename()));
    }

    @Override
    public String store(final GenericFile file, final InputStream stream) throws IOException {
        return uploadFile(dirnameFor(file), b -> b.field("file", stream, file.getFilename()));
    }

    @Override
    public String store(final GenericFile genericFile, final File file) throws IOException {
        return uploadFile(dirnameFor(genericFile), b -> b.field("file", file, genericFile.getFilename()));
    }

    @Override
    public byte[] read(final GenericFile file) {
        HttpResponse<byte[]> response = Unirest.get(getDriveUrl() + "/api/drive/file/" + file.getContentKey() + "/download")
                .header("Authorization", "Bearer " + getAccessToken())
                .asBytes();
        if (response.getStatus() == 307) {
            response = Unirest.get(response.getHeaders().getFirst("Location")).asBytes();
        }
        return response.getBody();
    }

    @Override
    public InputStream readAsInputStream(final GenericFile file) {
        final CompletableFuture<HttpResponse<byte[]>> future = Unirest.get(getDriveUrl() + "/api/drive/file/" + file.getContentKey() + "/download")
                .header("Authorization", "Bearer " + getAccessToken())
                .asBytesAsync();
        try {
            HttpResponse<byte[]> response = future.get();
            response = response.getStatus() == 307 ? Unirest.get(response.getHeaders().getFirst("Location"))
                    .asBytesAsync().get() : response;
            return new ByteArrayInputStream(response.getBody());
        } catch (final InterruptedException | ExecutionException e) {
            throw new Error(e);
        }
    }

}
