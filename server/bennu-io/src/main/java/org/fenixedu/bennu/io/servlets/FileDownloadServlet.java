package org.fenixedu.bennu.io.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.filters.CasAuthenticationFilter;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.io.domain.GenericFile;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Charsets;

@WebServlet(urlPatterns = FileDownloadServlet.SERVLET_PATH + "*")
public class FileDownloadServlet extends HttpServlet {
    private static final long serialVersionUID = -6697201298218837492L;

    static final String SERVLET_PATH = "/downloadFile/";

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
                if (sendFile(file, request, response)) {
                    return;
                }
                byte[] content = file.getContent();
                if (content != null) {
                    response.setContentType(file.getContentType());
                    response.setContentLength(file.getSize().intValue());
                    try (OutputStream stream = response.getOutputStream()) {
                        stream.write(content);
                        stream.flush();
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NO_CONTENT, "File empty");
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
    private boolean sendFile(GenericFile file, HttpServletRequest request, HttpServletResponse response) {
        if (supportsSendfile(request)) {
            Optional<String> filePath = FileStorage.sendfilePath(file);
            if (filePath.isPresent()) {
                handleSendfile(file, filePath.get(), request, response);
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
    private void handleSendfile(GenericFile file, String filename, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(file.getContentType());
        response.setContentLength(file.getSize().intValue());
        request.setAttribute("org.apache.tomcat.sendfile.filename", filename);
        request.setAttribute("org.apache.tomcat.sendfile.start", Long.valueOf(0l));
        request.setAttribute("org.apache.tomcat.sendfile.end", file.getSize());
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
