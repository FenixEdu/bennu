package org.fenixedu.bennu.io.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.io.domain.GenericFile;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

/**
 * Utility methods to handle the download of {@link GenericFile} instances. The methods provided by this class provide raw access
 * to the file, performing no validation of access control or URL matching.
 * 
 * The main purpose of this class is to allow all the existing features in file download (such as byte range serving, cache
 * controls, ETags, sendfile support, etc.) to be reused, independently of the way the user actually asks for the file.
 * 
 */
public class DownloadUtil {

    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d*)-(?<end>\\d*)");

    /**
     * Downloads the given file, based on the given request, into the provided response instance.
     * 
     * This method provides the same semantics as
     * {@link #downloadFile(GenericFile, HttpServletRequest, HttpServletResponse, String)}, using the default value for the Cache
     * Control header.
     * 
     * @param file
     *            The file to be downloaded
     * @param request
     *            The request which originated the download request
     * @param response
     *            The response into which the file should be written
     * @throws IOException
     *             If an IO error occurred while accessing or writing the file
     * @throws NullPointerException
     *             If either of the arguments is {@code null}
     * @see #downloadFile(GenericFile, HttpServletRequest, HttpServletResponse, String)
     */
    public static void downloadFile(GenericFile file, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        downloadFile(file, request, response, CoreConfiguration.getConfiguration().staticCacheControl());
    }

    /**
     * Downloads the given file, based on the given request, into the provided response instance, sending the provided cache
     * control header.
     * 
     * This methods does not provide any access control validation, and does not impose any limitation on the endpoint which is
     * exposing the file.
     * 
     * @param file
     *            The file to be downloaded
     * @param request
     *            The request which originated the download request
     * @param response
     *            The response into which the file should be written
     * @param cacheControl
     *            The value of the Cache-Control header, may be {@code null}
     * @throws IOException
     *             If an IO error occurred while accessing or writing the file
     * @throws NullPointerException
     *             If either the file, request or response is {@code null}
     */
    public static void downloadFile(GenericFile file, HttpServletRequest request, HttpServletResponse response,
            String cacheControl) throws IOException {
        String etag = "W/\"" + file.getExternalId() + "\"";
        response.setHeader("ETag", etag);
        if (cacheControl != null) {
            response.setHeader("Cache-Control", cacheControl);
        }
        response.setHeader("Accept-Ranges", "bytes");
        response.setContentType(file.getContentType());

        if (etag.equals(request.getHeader("If-None-Match"))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        long length = file.getSize();
        long start = 0;
        long end = length - 1;

        String range = request.getHeader("Range");
        if (range != null) {
            Matcher matcher = RANGE_PATTERN.matcher(range);
            boolean match = matcher.matches();
            if (match) {
                try {
                    String startGroup = matcher.group("start");
                    start = Strings.isNullOrEmpty(startGroup) ? start : Long.valueOf(startGroup);
                    start = start < 0 ? 0 : start;

                    String endGroup = matcher.group("end");
                    end = Strings.isNullOrEmpty(endGroup) ? end : Long.valueOf(endGroup);
                    end = end > length - 1 ? length - 1 : end;
                } catch (NumberFormatException e) {
                    match = false;
                }
            }
            if (!match) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
        }

        long contentLength = end - start + 1;

        response.setHeader("Content-Length", Long.toString(contentLength));
        if (range != null) {
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        if (sendFile(file, request, response, start, end)) {
            return;
        }
        try (InputStream stream = file.getStream()) {
            if (stream != null) {
                try (OutputStream out = response.getOutputStream()) {
                    copyStream(stream, out, start, contentLength);
                    out.flush();
                }
            } else {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "File empty");
            }
        }
    }

    private static final int BUF_SIZE = 0x1000; // 4K

    private static void copyStream(InputStream in, OutputStream out, long start, long bytesToRead) throws IOException {
        ByteStreams.skipFully(in, start);
        byte buffer[] = new byte[BUF_SIZE];
        int len = buffer.length;
        while ((bytesToRead > 0) && (len >= buffer.length)) {
            len = in.read(buffer);
            if (bytesToRead >= len) {
                out.write(buffer, 0, len);
                bytesToRead -= len;
            } else {
                out.write(buffer, 0, (int) bytesToRead);
                bytesToRead = 0;
            }
            if (len < buffer.length) {
                break;
            }
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
    private static boolean sendFile(GenericFile file, HttpServletRequest request, HttpServletResponse response, long start,
            long end) {
        if (supportsSendfile(request)) {
            Optional<String> filePath = FileStorage.sendfilePath(file);
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

}