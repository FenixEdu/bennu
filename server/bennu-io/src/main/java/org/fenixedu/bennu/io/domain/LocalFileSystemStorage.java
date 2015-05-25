package org.fenixedu.bennu.io.domain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jvstm.PerTxBox;

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

    private PerTxBox<Map<String, FileWriteIntention>> fileIntentions;

    private static class FileWriteIntention {

        private final String path;
        private final byte[] contents;

        FileWriteIntention(String path, byte[] contents) {
            this.path = path;
            this.contents = contents;
        }

        public void write() throws IOException {
            Files.write(contents, new File(path));
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
    public String store(String uniqueIdentification, byte[] content) {

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
    public byte[] read(String uniqueIdentification) {
        try {
            Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map.containsKey(uniqueIdentification)) {
                return map.get(uniqueIdentification).contents;
            }

            return Files.toByteArray(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (IOException e) {
            throw new RuntimeException("error.store.file", e);
        }
    }

    @Override
    public InputStream readAsInputStream(String uniqueIdentification) {
        try {
            Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map.containsKey(uniqueIdentification)) {
                return new ByteArrayInputStream(map.get(uniqueIdentification).contents);
            }

            return new FileInputStream(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("file.not.found", e);
        }
    }

    /*
     * Returns the absolute path for the given content key.
     * 
     * It must first check if the file indeed exists, in order
     * for the application to throw the proper exception.
     */
    @Override
    Optional<String> getSendfilePath(String uniqueIdentification) {
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
