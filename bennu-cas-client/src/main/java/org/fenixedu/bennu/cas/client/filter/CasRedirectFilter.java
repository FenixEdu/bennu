package org.fenixedu.bennu.cas.client.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.cas.client.CASClientConfiguration;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class CasRedirectFilter implements Filter {

    private int contextPathLength = -1;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        contextPathLength = servletContext.getContextPath().length();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();
        String path = trim(requestURI);
        boolean isUserLogged = Authenticate.getUser() != null;
        Boolean hasCasCookie = hasActiveCasCookie(request);

        if (isUserLogged && path.contains("logout") && hasCasCookie) {
            // Invalidates CAS cookie if a logout is performed
            Cookie cookie = BennuFilterUtils.getCookie(request, "redirectToCas");
            cookie.setValue("false");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (!isUserLogged && CASClientConfiguration.getConfiguration().casEnabled() && hasCasCookie
                && isNotFileOrUIlayerUrl(request, path) && !path.equals("login/cas") && !path.contains("cas-client/login")) {
            // In a multi AS environment this callback allows to go to another AS and stay in the same page 
            // or at least in the page that started the navigation flow when the current AS is shutdown
            addCallback(path, session);

            String actualCallbackToUse = CASClientConfiguration.getConfiguration().casServiceUrl();
            Escaper escaper = UrlEscapers.urlPathSegmentEscaper();
            actualCallbackToUse = Base64.getUrlEncoder().encodeToString(actualCallbackToUse.getBytes(StandardCharsets.UTF_8));
            response.sendRedirect(CASClientConfiguration.getConfiguration().casServerUrl() + "/login?service=" + escaper.escape(
                    CoreConfiguration.getConfiguration().applicationUrl() + "/api/cas-client/login/" + actualCallbackToUse));
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    private Boolean hasActiveCasCookie(HttpServletRequest request) {
        Cookie cookie = BennuFilterUtils.getCookie(request, "redirectToCas");
        return cookie != null && cookie.getValue().equals("true");
    }

    private void addCallback(String path, HttpSession session) {
        MenuFunctionality menuEntry = getMenuEntry(path);
        if (path != null && !path.isBlank() && menuEntry != null) {
            session.setAttribute(BennuFilterUtils.CALLBACK_URL, path);
        }
    }

    private MenuFunctionality getMenuEntry(String path) {
        MenuFunctionality menu = path == null ? null : PortalConfiguration.getInstance().getMenu()
                .findFunctionalityWithPathWithoutAccessControl(path.split("/"));
        return menu;
    }

    private boolean isNotFileOrUIlayerUrl(HttpServletRequest request, String path) {
        return path != null && !path.isBlank() ? BennuFilterUtils.getFilesAndUiLayerExceptions().stream()
                .noneMatch(p -> p.test(request, path)) : false;
    }

    private String trim(String value) {
        int len = value.length();
        int st = contextPathLength;
        char[] val = value.toCharArray();

        while ((st < len) && (val[st] == '/')) {
            st++;
        }
        while ((st < len) && (val[len - 1] == '/')) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

    @Override
    public void destroy() {

    }

}
