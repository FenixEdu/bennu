package org.fenixedu.bennu.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieReaderUtils {

    /**
     * Searches for a cookie within a HttpServletRequest.
     * 
     * @param name the name of the cookie.
     * @param request the HttpServletRequest to find the cookie on.
     * @return
     */
    public static Cookie getCookieForName(String name, HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie;
            }
        }
        return null;
    }

}
