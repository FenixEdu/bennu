package org.fenixedu.bennu.io.domain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jvstm.PerTxBox;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.io.Files;

/**
 * 
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
public class LocalFileSystemStorage extends LocalFileSystemStorage_Base {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystemStorage.class);

    private transient PerTxBox<Map<String, FileWriteIntention>> fileIntentions;

    private static class FileWriteIntention {

        private final String path;
        private final byte[] contents;
        private final File file;

        FileWriteIntention(String path, byte[] contents) {
            this.path = path;
            this.contents = contents;
            this.file = null;
        }

        FileWriteIntention(String path, File file) {
            this.path = path;
            this.contents = null;
            this.file = file;
        }

        public void write() throws IOException {
            if (contents != null) {
                Files.write(contents, new File(path));
            } else {
                java.nio.file.Files.move(file.toPath(), Paths.get(path));
            }
        }

        public byte[] asByteArray() throws IOException {
            if (contents != null) {
                return contents;
            } else {
                return Files.toByteArray(file);
            }
        }

        public InputStream asInputStream() throws IOException {
            if (contents != null) {
                return new ByteArrayInputStream(contents);
            } else {
                return new FileInputStream(file);
            }
        }
    }

    LocalFileSystemStorage(String name, String path, Integer treeDirectoriesNameLength) {
        super();
        setName(name);
        setPath(path);
        setTreeDirectoriesNameLength(treeDirectoriesNameLength);
    }

    @Override
    public String getPath() {
        //FIXME: remove when the framework enables read-only slots
        return super.getPath();
    }

    @Override
    public Integer getTreeDirectoriesNameLength() {
        //FIXME: remove when the framework enables read-only slots
        return super.getTreeDirectoriesNameLength();
    }

    @Override
    public String store(GenericFile genericFile, File file) {
        String uniqueIdentification =
                genericFile.getContentKey() == null ? genericFile.getExternalId() : genericFile.getContentKey();
        final String fullPath = getFullPath(uniqueIdentification);

        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        } else {
            if (!directory.isDirectory()) {
                throw new RuntimeException("Trying to create " + fullPath
                        + " as a directory but, it already exists and it's not a directory");
            }
        }

        Map<String, FileWriteIntention> map = new HashMap<>(getPerTxBox().get());
        map.put(uniqueIdentification, new FileWriteIntention(fullPath + uniqueIdentification, file));
        getPerTxBox().put(map);
        return uniqueIdentification;

    }

    @Override
    public String store(GenericFile file, byte[] content) {
        String uniqueIdentification = file.getContentKey() == null ? file.getExternalId() : file.getContentKey();

        final String fullPath = getFullPath(uniqueIdentification);

        if (content == null) {
            new LocalFileToDelete(fullPath + uniqueIdentification);
        } else {
            File directory = new File(fullPath);
            if (!directory.exists()) {
                directory.mkdirs();
            } else {
                if (!directory.isDirectory()) {
                    throw new RuntimeException("Trying to create " + fullPath
                            + " as a directory but, it already exists and it's not a directory");
                }
            }

            Map<String, FileWriteIntention> map = new HashMap<>(getPerTxBox().get());
            map.put(uniqueIdentification, new FileWriteIntention(fullPath + uniqueIdentification, content));
            getPerTxBox().put(map);
        }
        return uniqueIdentification;

    }

    private String getFullPath(final String uniqueIdentification) {
        return getAbsolutePath() + transformIDInPath(uniqueIdentification) + File.separatorChar;
    }

    private static final Pattern BRACES_PATTERN = Pattern.compile("(\\{.+?\\})");

    public String getAbsolutePath() {
        String path = getPath();
        if (path.indexOf("{") != -1 && path.indexOf("}") != -1) {
            // Compile regular expression
            Matcher matcher = BRACES_PATTERN.matcher(path);

            // Replace all occurrences of pattern in input
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String replaceStr = CharMatcher.anyOf("{}").trimFrom(matcher.group());
                matcher.appendReplacement(result, System.getProperty(replaceStr));
            }
            matcher.appendTail(result);
            path = result.toString();
        }
        if (!path.endsWith(File.separator)) {
            path = path + File.separatorChar;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            logger.debug("Filesystem storage {} directory does not exist, creating: {}", getName(), path);
            dir.mkdir();
        }
        return path;
    }

    private String transformIDInPath(final String uniqueIdentification) {
        final Integer directoriesNameLength = getTreeDirectoriesNameLength();

        final StringBuilder result = new StringBuilder();

        char[] idArray = uniqueIdentification.toCharArray();
        for (int i = 0; i < idArray.length; i++) {
            if (i > 0 && i % directoriesNameLength == 0 && ((i + directoriesNameLength) < uniqueIdentification.length())) {
                result.append(File.separatorChar);
            } else if ((i + directoriesNameLength) >= uniqueIdentification.length()) {
                break;
            }
            result.append(idArray[i]);
        }

        return result.toString();
    }

    @Override
    public byte[] read(GenericFile file) {
        String uniqueIdentification = file.getContentKey();
        try {
            Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map.containsKey(uniqueIdentification)) {
                return map.get(uniqueIdentification).asByteArray();
            }

            return Files.toByteArray(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (IOException e) {
            throw new RuntimeException("error.store.file", e);
        }
    }

    @Override
    public InputStream readAsInputStream(GenericFile file) {
        String uniqueIdentification = file.getContentKey();
        try {
            Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map.containsKey(uniqueIdentification)) {
                return map.get(uniqueIdentification).asInputStream();
            }

            return new FileInputStream(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("file.not.found", e);
        }
    }

    /*
     * Attempt to use the 'sendfile' primitive to download the file.
     * 
     * This feature may not be supported, or the file may not be stored in the
     * filesystem, causing this not to work.
     * 
     * However, when it works, it provides great benefits, as the file must not
     * be read to the Java Heap, only to be written to a socket, thus greatly
     * reducing memory consumption.
     */
    @Override
    protected boolean tryLowLevelDownload(GenericFile file, HttpServletRequest request, HttpServletResponse response, long start,
            long end) {
        if (supportsSendfile(request)) {
            Optional<String> filePath = getSendfilePath(file.getContentKey());
            if (filePath.isPresent()) {
                handleSendfile(file, filePath.get(), request, response, start, end);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /*
     * Sendfile is available, and the file is stored in the filesystem, so instruct
     * the container to directly write the file.
     * 
     * For now, we only support the Tomcat-specific 'sendfile' implementation.
     * See: http://tomcat.apache.org/tomcat-7.0-doc/aio.html#Asynchronous_writes
     */
    private static void handleSendfile(GenericFile file, String filename, HttpServletRequest request,
            HttpServletResponse response, long start, long end) {
        response.setHeader("X-Bennu-Sendfile", "true");
        request.setAttribute("org.apache.tomcat.sendfile.filename", filename);
        request.setAttribute("org.apache.tomcat.sendfile.start", Long.valueOf(start));
        request.setAttribute("org.apache.tomcat.sendfile.end", Long.valueOf(end + 1));
    }

    /*
     * Checks if the container supports usage of the 'sendfile' primitive.
     * 
     * For now, we only support the Tomcat-specific 'sendfile' implementation. 
     * See: http://tomcat.apache.org/tomcat-7.0-doc/aio.html#Asynchronous_writes
     */
    private static boolean supportsSendfile(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.sendfile.support"));
    }

    /*
     * Returns the absolute path for the given content key.
     * 
     * It must first check if the file indeed exists, in order
     * for the application to throw the proper exception.
     */
    private Optional<String> getSendfilePath(String uniqueIdentification) {
        String path = getFullPath(uniqueIdentification) + uniqueIdentification;
        if (new File(path).exists()) {
            return Optional.of(path);
        } else {
            return Optional.empty();
        }
    }

    private synchronized PerTxBox<Map<String, FileWriteIntention>> getPerTxBox() {
        if (fileIntentions == null) {
            fileIntentions = new PerTxBox<Map<String, FileWriteIntention>>(Collections.emptyMap()) {
                @Override
                public void commit(Map<String, FileWriteIntention> map) {
                    for (FileWriteIntention i : map.values()) {
                        try {
                            i.write();
                        } catch (IOException e) {
                            throw new RuntimeException("error.store.file", e);
                        }
                    }
                }
            };
        }
        return fileIntentions;
    }
}
