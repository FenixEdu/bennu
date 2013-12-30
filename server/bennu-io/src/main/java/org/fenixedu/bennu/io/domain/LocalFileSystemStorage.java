package org.fenixedu.bennu.io.domain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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

        private String path;
        private byte[] contents;

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

            Map<String, FileWriteIntention> map = getPerTxBox().get();
            if (map == null) {
                map = new HashMap<>();
                fileIntentions.put(map);
            }
            if (map.containsKey(uniqueIdentification)) {
                map.remove(uniqueIdentification);
            }
            map.put(uniqueIdentification, new FileWriteIntention(fullPath + uniqueIdentification, content));
        }
        return uniqueIdentification;

    }

    private String getFullPath(final String uniqueIdentification) {
        return getAbsolutePath() + transformIDInPath(uniqueIdentification) + File.pathSeparator;
    }

    public String getAbsolutePath() {
        String path = getPath();
        if (path.indexOf("{") != -1 && path.indexOf("}") != -1) {
            // Compile regular expression
            Matcher matcher = Pattern.compile("(\\{.+?\\})").matcher(path);

            // Replace all occurrences of pattern in input
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String replaceStr = CharMatcher.anyOf("{}").trimFrom(matcher.group());
                matcher.appendReplacement(result, System.getProperty(replaceStr));
            }
            matcher.appendTail(result);
            path = result.toString();
        }
        if (!path.endsWith(File.pathSeparator)) {
            path = path + File.pathSeparator;
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
                result.append(File.pathSeparator);
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
            if (map != null && map.containsKey(uniqueIdentification)) {
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
            if (map != null && map.containsKey(uniqueIdentification)) {
                return new ByteArrayInputStream(map.get(uniqueIdentification).contents);
            }

            return new FileInputStream(new File(getFullPath(uniqueIdentification) + uniqueIdentification));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("file.not.found", e);
        }
    }

    private synchronized PerTxBox<Map<String, FileWriteIntention>> getPerTxBox() {
        if (fileIntentions == null) {
            fileIntentions = new PerTxBox<Map<String, FileWriteIntention>>(null) {
                @Override
                public void commit(Map<String, FileWriteIntention> map) {
                    for (String key : map.keySet()) {
                        try {
                            map.get(key).write();
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
