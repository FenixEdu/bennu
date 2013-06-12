package pt.ist.bennu.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

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
            if (StringUtils.equalsIgnoreCase(cookie.getName(), name)) {
                return cookie;
            }
        }
        return null;
    }

}
