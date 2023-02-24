package org.fenixedu.bennu.cas.client.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class BennuFilterUtils {

    public static final String CALLBACK_URL = "CALLBACK_URL";

    private static List<BiPredicate<HttpServletRequest, String>> FILES_AND_UILAYER_EXCEPTIONS = new ArrayList<>();

    public static void addFileOrUiLayerException(BiPredicate<HttpServletRequest, String> predicate) {
        FILES_AND_UILAYER_EXCEPTIONS.add(predicate);
    }

    public static List<BiPredicate<HttpServletRequest, String>> getFilesAndUiLayerExceptions() {
        return FILES_AND_UILAYER_EXCEPTIONS;
    }

    static {
        addFileOrUiLayerException((req, path) -> path.endsWith(".js"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".css"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".scss"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".eot"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".otf"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".png"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".jpg"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".jpeg"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".gif"));
        addFileOrUiLayerException((req, path) -> path.endsWith("favicon"));
        addFileOrUiLayerException((req, path) -> path.endsWith("logo"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".svg"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".less"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".ico"));
        // Added because it was being fetched after cas-client/login request
        addFileOrUiLayerException((req, path) -> path.endsWith(".css.map"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".ttf"));
        addFileOrUiLayerException((req, path) -> path.endsWith(".woff"));
        addFileOrUiLayerException(
                (req, path) -> path.startsWith(req.getContextPath().isBlank() ? "uiLayer" : req.getContextPath() + "/uiLayer"));
        addFileOrUiLayerException((req, path) -> path.startsWith(req.getContextPath() + "/VAADIN"));

    }

    /***
     * Retrieves cookie from http request
     * 
     * @param request
     * @param cookieName
     * @return Cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    cookie = c;
                    break;
                }
            }
        }
        return cookie;
    }
}