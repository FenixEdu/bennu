package org.fenixedu.bennu.io.domain;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.io.ByteStreams;
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
        private final InputStream stream;
    
        FileWriteIntention(final String path, final byte[] contents) {
            this.path = path;
            this.contents = contents;
            this.file = null;
            this.stream = null;
        }

        FileWriteIntention(final String path, final File file) {
            this.path = path;
            this.contents = null;
            this.file = file;
            this.stream = null;
        }
    
        public FileWriteIntention(final String path, final InputStream stream) {
            this.path = path;
            this.contents = null;
            this.file = null;
            this.stream = stream;
        }
    
        public void write() throws IOException {
            if (contents != null) {
                Files.write(contents, new File(path));
            } else if (stream!=null){
                final File file = new File(path);
                final OutputStream outputStream = new FileOutputStream(file);
                ByteStreams.copy(stream, outputStream);
                outputStream.close();
            } else {
                java.nio.file.Files.move(file.toPath(), Paths.get(path));
            }
        }

        public byte[] asByteArray() throws IOException {
            return contents == null ? Files.toByteArray(file) : contents;
        }

        public InputStream asInputStream() throws IOException {
            return contents == null ? new FileInputStream(file) : new ByteArrayInputStream(contents);
        }
    }

    LocalFileSystemStorage(final String name, final String path, final Integer treeDirectoriesNameLength) {
        setName(name);
        setPath(path);
        setTreeDirectoriesNameLength(treeDirectoriesNameLength);
    }

    @Override
    public String getPath() {
        return super.getPath();
    }

    @Override
    public Integer getTreeDirectoriesNameLength() {
        return super.getTreeDirectoriesNameLength();
    }

    private String store(final GenericFile genericFile, final Function<String, FileWriteIntention> intentionProvider) {
        final String uniqueIdentification =
                genericFile.getContentKey() == null ? genericFile.getExternalId() : genericFile.getContentKey();
        final String fullPath = getFullPath(uniqueIdentification);

        ensureDirectoryExists(fullPath);

        final Map<String, FileWriteIntention> map = new HashMap<>(getPerTxBox().get());
        map.put(uniqueIdentification, intentionProvider.apply(fullPath + uniqueIdentification));
        getPerTxBox().put(map);
        return uniqueIdentification;
    }

    @Override
    public String store(final GenericFile genericFile, final File file) {
        return store(genericFile, s -> new FileWriteIntention(s, file));
    }
    
    @Override
    public String store(final GenericFile genericFile, final InputStream stream) {
        return store(genericFile, s -> new FileWriteIntention(s, stream));
    }
    
    @Override
    public String store(final GenericFile file, final byte[] content) {
        final String uniqueIdentification = file.getContentKey() == null ? file.getExternalId() : file.getContentKey();

        final String fullPath = getFullPath(uniqueIdentification);

        if (content == null) {
            new LocalFileToDelete(fullPath + uniqueIdentification);
        } else {
            ensureDirectoryExists(fullPath);

            final Map<String, FileWriteIntention> map = new HashMap<>(getPerTxBox().get());
            map.put(uniqueIdentification, new FileWriteIntention(fullPath + uniqueIdentification, content));
            getPerTxBox().put(map);
        }
        return uniqueIdentification;

    }

    private static void ensureDirectoryExists(final String fullPath) {
        final File directory = new File(fullPath);
        if (!directory.exists()) {
            if (!directory.mkdirs() && !directory.exists()) {
                throw new RuntimeException("Could not create directory " + directory.getAbsolutePath());
            }
        } else {
            if (!directory.isDirectory()) {
                throw new RuntimeException("Trying to create " + fullPath
                        + " as a directory but, it already exists and it's not a directory");
            }
        }
    }

    private String getFullPath(final String uniqueIdentification) {
        return getAbsolutePath() + transformIDInPath(uniqueIdentification) + File.separatorChar;
    }

    private static final Pattern BRACES_PATTERN = Pattern.compile("(\\{.+?\\})");

    public String getAbsolutePath() {
        String path = getPath();
        if (path.contains("{") && path.contains("}")) {
            // Compile regular expression
            final Matcher matcher = BRACES_PATTERN.matcher(path);

            // Replace all occurrences of pattern in input
            final StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                final String replaceStr = CharMatcher.anyOf("{}").trimFrom(matcher.group());
                matcher.appendReplacement(result, System.getProperty(replaceStr));
            }
            matcher.appendTail(result);
            path = result.toString();
        }
        if (!path.endsWith(File.separator)) {
            path = path + File.separatorChar;
        }
        final File dir = new File(path);
        if (!dir.exists()) {
            logger.debug("Filesystem storage {} directory does not exist, creating: {}", getName(), path);
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create base directory for " + this.getExternalId() + ": " + path);
            }
        }
        return path;
    }

    private String transformIDInPath(final String uniqueIdentification) {
        final Integer directoriesNameLength = getTreeDirectoriesNameLength();

        final StringBuilder result = new StringBuilder();

        final char[] idArray = uniqueIdentification.toCharArray();
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
    public byte[] read(final GenericFile file) {
        final String uniqueIdentification = file.getContentKey();
        try {
            final Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map.containsKey(uniqueIdentification)) {
                return map.get(uniqueIdentification).asByteArray();
            }

            return Files.toByteArray(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (final IOException e) {
            throw new RuntimeException("error.store.file", e);
        }
    }

    @Override
    public InputStream readAsInputStream(final GenericFile file) {
        final String uniqueIdentification = file.getContentKey();
        try {
            final Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map.containsKey(uniqueIdentification)) {
                return map.get(uniqueIdentification).asInputStream();
            }

            return new FileInputStream(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (final IOException e) {
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
    protected boolean tryLowLevelDownload(final GenericFile file, final HttpServletRequest request,
                                          final HttpServletResponse response, final long start, final long end) {
        if (supportsSendfile(request)) {
            final Optional<String> filePath = getSendfilePath(file.getContentKey());
            if (filePath.isPresent()) {
                handleSendfile(filePath.get(), request, response, start, end);
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
    private static void handleSendfile(final String filename, final HttpServletRequest request,
                                       final HttpServletResponse response, final long start, final long end) {
        response.setHeader("X-Bennu-Sendfile", "true");
        request.setAttribute("org.apache.tomcat.sendfile.filename", filename);
        request.setAttribute("org.apache.tomcat.sendfile.start", start);
        request.setAttribute("org.apache.tomcat.sendfile.end", end + 1);
    }

    /*
     * Checks if the container supports usage of the 'sendfile' primitive.
     * 
     * For now, we only support the Tomcat-specific 'sendfile' implementation. 
     * See: http://tomcat.apache.org/tomcat-7.0-doc/aio.html#Asynchronous_writes
     */
    private static boolean supportsSendfile(final HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.sendfile.support"));
    }

    /*
     * Returns the absolute path for the given content key.
     * 
     * It must first check if the file indeed exists, in order
     * for the application to throw the proper exception.
     */
    private Optional<String> getSendfilePath(final String uniqueIdentification) {
        final String path = getFullPath(uniqueIdentification) + uniqueIdentification;
        return new File(path).exists() ? Optional.of(path) : Optional.empty();
    }

    private synchronized PerTxBox<Map<String, FileWriteIntention>> getPerTxBox() {
        if (fileIntentions == null) {
            fileIntentions = new PerTxBox<Map<String, FileWriteIntention>>(Collections.emptyMap()) {
                @Override
                public void commit(final Map<String, FileWriteIntention> map) {
                    for (final FileWriteIntention i : map.values()) {
                        try {
                            i.write();
                        } catch (final IOException e) {
                            throw new RuntimeException("error.store.file", e);
                        }
                    }
                }
            };
        }
        return fileIntentions;
    }
}
