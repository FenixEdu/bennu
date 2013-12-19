package org.fenixedu.bennu.portal.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CookieReaderUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevelopmentModeFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentModeFilter.class);

    private static final String DEVELOPMENT_COOKIE_NAME = "developmentMode";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Cookie cookie = CookieReaderUtils.getCookieForName(DEVELOPMENT_COOKIE_NAME, httpServletRequest);
        if (CoreConfiguration.getConfiguration().developmentMode()) {
            // ON DEVMODE, LET'SE CHECK IF COOKIE DEV MODE IS TRUE
            if (cookie == null || !cookie.getValue().equals("true")) {
                logger.info("Development Mode Active! Setting developmentMode cookie");
                Cookie newCookie = new Cookie(DEVELOPMENT_COOKIE_NAME, "true");
                newCookie.setPath("/");
                httpServletResponse.addCookie(newCookie);
            }
        } else {
            // NOT ON DEV MODE: IF COOKIE EXISTS, LETS DELETE IT
            if (cookie != null) {
                Cookie newCookie = new Cookie(DEVELOPMENT_COOKIE_NAME, null);
                newCookie.setPath("/");
                newCookie.setMaxAge(0);
                httpServletResponse.addCookie(newCookie);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
