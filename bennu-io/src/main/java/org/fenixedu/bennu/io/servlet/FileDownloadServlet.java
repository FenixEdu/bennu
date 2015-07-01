package org.fenixedu.bennu.io.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.servlet.CasAuthenticationFilter;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.io.domain.GenericFile;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

@WebServlet(urlPatterns = FileDownloadServlet.SERVLET_PATH + "*")
public class FileDownloadServlet extends HttpServlet {
    private static final long serialVersionUID = -6697201298218837492L;

    static final String SERVLET_PATH = "/downloadFile/";
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d*)-(?<end>\\d*)");

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final GenericFile file = getFileFromURL(request.getRequestURI());
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        } else {
            if (request.getPathInfo().equals("/" + file.getExternalId())) {
                response.sendRedirect(getDownloadUrl(file));
                return;
            }
            if (file.isAccessible(Authenticate.getUser())) {
                String etag = "W/\"" + file.getExternalId() + "\"";
                response.setHeader("ETag", etag);
                response.setHeader("Cache-Control", "max-age=43200");
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
            } else if (file.isPrivate() && !Authenticate.isLogged()
                    && request.getAttribute(CasAuthenticationFilter.AUTHENTICATION_EXCEPTION_KEY) == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendRedirect(sendLoginRedirect(file));
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "File not accessible");
            }
        }
    }

    private static final int BUF_SIZE = 0x1000; // 4K

    private void copyStream(InputStream in, OutputStream out, long start, long bytesToRead) throws IOException {
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
    private boolean sendFile(GenericFile file, HttpServletRequest request, HttpServletResponse response, long start, long end) {
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
    private void handleSendfile(GenericFile file, String filename, HttpServletRequest request, HttpServletResponse response,
            long start, long end) {
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
    private boolean supportsSendfile(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.sendfile.support"));
    }

    public static String getDownloadUrl(GenericFile file) {
        return CoreConfiguration.getConfiguration().applicationUrl() + SERVLET_PATH + file.getExternalId() + "/"
                + file.getFilename();
    }

    private String sendLoginRedirect(final GenericFile file) throws IOException {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/login?callback="
                + URLEncoder.encode(getDownloadUrl(file), Charsets.UTF_8.name());
    }

    public static GenericFile getFileFromURL(String url) {
        try {
            // Remove trailing path, and split the tokens
            String[] parts = url.substring(url.indexOf(SERVLET_PATH)).replace(SERVLET_PATH, "").split("\\/");
            if (parts.length == 0) {
                return null;
            }
            DomainObject object = FenixFramework.getDomainObject(parts[0]);
            if (object instanceof GenericFile && FenixFramework.isDomainObjectValid(object)) {
                return (GenericFile) object;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
