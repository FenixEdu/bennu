package org.fenixedu.bennu.io.domain;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.io.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.drive.sdk.ClientFactory;
import pt.ist.drive.sdk.DriveClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriveStorage extends DriveStorage_Base {

    private DriveClient driveClient;
    private String baseDirectory;

    private static Logger logger = LoggerFactory.getLogger(DriveStorage.class);


    public DriveStorage(String name, String driveUrl, String appId, String appUser, String refreshToken, String storeDirectorySlug, int fileIdLength,
            final int folderNameMaxLength) {
        super();
        setName(name);
        setDriveUrl(driveUrl);
        setAppId(appId);
        setAppUser(appUser);
        setRefreshToken(refreshToken);
        setStoreDirectorySlug(storeDirectorySlug);
        setFileIdLength(fileIdLength);
        setFolderNameMaxLength(folderNameMaxLength);
    }

    public DriveClient getDriveClient() {
        if (driveClient == null) {
            driveClient = ClientFactory.driveCLient(getDriveUrl(), getAppId(), getAppUser(), getRefreshToken());
            baseDirectory = driveClient.getDirectoryWithSlug(getStoreDirectorySlug()).get("id").getAsString();
        }
        return driveClient;
    }

    @Override
    protected boolean tryRedirectDownloadFile(final GenericFile file, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String redirectUrl = getDriveClient().getDownloadRedirectUrl(getDriveFileId(file));
        if (redirectUrl != null) {
            response.sendRedirect(redirectUrl);
            return true;
        }
        return false;
    }

    @Override
    public String store(GenericFile file, byte[] content) {
        String randomId = IdGenerator.generateSecureId(getFileIdLength()).toLowerCase();
        List<String> pathList = Arrays.asList(randomId.split("(?<=\\G.{" + getFolderNameMaxLength() + "})"));

        String path = Joiner.on("/").join(pathList.subList(0, pathList.size() - 1));
//        String filename = Joiner.on("").join(pathList);
        String filename = file.getDisplayName();

        String fileId = path + "/" + filename;

        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            JsonObject result =
                    getDriveClient().uploadWithInfoCreatePath(baseDirectory, filename, inputStream, "application/octet-stream", path);
            logger.debug("DriveStorage file saved with id: ", result.get("id"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileId;
    }

    @Override
    public byte[] read(GenericFile file) {
        try {
            return ByteStreams.toByteArray(readAsInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("File not found.");
        }
    }

    @Override
    public InputStream readAsInputStream(GenericFile file) {
        try {
            return getDriveClient().downloadFile(getDriveFileId(file));
        }
        catch (IOException e) {
            throw new RuntimeException("File not found.");
        }
    }

    private String getDriveFileId(final GenericFile file) {
        String identifier = file.getContentKey();

        List<String> pathList = Arrays.asList(identifier.split("/"));

        List<String> completePath = new ArrayList<>();
        completePath.add(getStoreDirectorySlug());
        completePath.addAll(pathList.subList(0, pathList.size() - 1));

        JsonObject directoryWithSlug = getDriveClient().getDirectoryWithSlug(completePath);

        JsonArray asJsonArray = directoryWithSlug.get("items").getAsJsonArray();

        return asJsonArray.get(0).getAsJsonObject().get("id").getAsString();
    }

    @Override
    public String getDriveUrl() {
        return super.getDriveUrl();
    }

    @Override
    public String getAppId() {
        return super.getAppId();
    }

    @Override
    public String getAppUser() {
        return super.getAppUser();
    }

    @Override
    public String getRefreshToken() {
        return super.getRefreshToken();
    }

    @Override
    public String getStoreDirectorySlug() {
        return super.getStoreDirectorySlug();
    }

    @Override public int getFileIdLength() {
        return super.getFileIdLength();
    }

    @Override public int getFolderNameMaxLength() {
        return super.getFolderNameMaxLength();
    }
}
