package org.fenixedu.bennu.io.domain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.SignatureAlgorithm;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
import org.apache.commons.io.IOUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.jwt.Tools;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class DriveAPIStorage extends DriveAPIStorage_Base {

    static {
        Unirest.config().reset();
        Unirest.config().followRedirects(false);
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
        return result.get("id").getAsString();
    }

    private InputStream downloadFile(final String fileId) {
        final CompletableFuture<HttpResponse<InputStream>> future = Unirest.get(getDriveUrl() + "/api/drive/file/" + fileId + "/download")
                .header("Authorization", "Bearer " + getAccessToken())
                .asObjectAsync(raw -> raw.getContent());
        try {
            HttpResponse<InputStream> response = future.get();
            response = response.getStatus() == 307 ? Unirest.get(response.getHeaders().getFirst("Location"))
                    .asObjectAsync(raw -> raw.getContent()).get() : response;
            return response.getBody();
        } catch (final InterruptedException | ExecutionException e) {
            throw new Error(e);
        }
    }

    private boolean deleteFile(final String fileId) {
        final HttpResponse<String> response = Unirest.delete(getDriveUrl() + "/api/drive/file/" + fileId)
                .header("Authorization", "Bearer " + getAccessToken())
                .header("X-Requested-With", "XMLHttpRequest")
                .asString();
        return response.getStatus() == 204;
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
        try {
            try (final InputStream inputStream = readAsInputStream(file)) {
                return IOUtils.toByteArray(inputStream);
            }
        } catch (final IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public InputStream readAsInputStream(final GenericFile file) {
        return downloadFile(file.getContentKey());
    }

}
