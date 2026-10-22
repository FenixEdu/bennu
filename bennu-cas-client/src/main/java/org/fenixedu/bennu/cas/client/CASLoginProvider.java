package org.fenixedu.bennu.cas.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class CASLoginProvider implements LoginProvider {

    /**
     * Session attribute set when a session is authenticated through CAS, so that the logout hook
     * only claims sessions it actually owns (other providers' or the application's own sessions
     * get the plain local logout).
     */
    public static final String CAS_SESSION_ATTRIBUTE = "CAS_CLIENT_AUTHENTICATED_SESSION";

    private final Escaper escaper = UrlEscapers.urlPathSegmentEscaper();

    @Override
    public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback) throws IOException,
            ServletException {
        if (Strings.isNullOrEmpty(callback)) {
            callback = CASClientConfiguration.getConfiguration().casServiceUrl();
        }
        callback = Base64.getUrlEncoder().encodeToString(UrlEscapers.urlFragmentEscaper().escape(callback).getBytes(StandardCharsets.UTF_8));
        response.sendRedirect(CASClientConfiguration.getConfiguration().casServerUrl() + "/login?service="
                + escaper.escape(CoreConfiguration.getConfiguration().applicationUrl() + "/api/cas-client/login/" + callback));
    }

    @Override
    public String getKey() {
        return "cas";
    }

    @Override
    public String getName() {
        return "CAS";
    }

    @Override
    public boolean isEnabled() {
        return CASClientConfiguration.getConfiguration().casEnabled();
    }

    @Override
    public boolean logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute(CAS_SESSION_ATTRIBUTE))) {
            // not a CAS-authenticated session: plain local logout is enough
            return false;
        }
        String casServerUrl = CASClientConfiguration.getConfiguration().casServerUrl();
        if (Strings.isNullOrEmpty(casServerUrl)) {
            return false;
        }
        // Terminate the CAS SSO session; the caller terminates the local session right after.
        response.sendRedirect(casServerUrl + "/logout");
        return true;
    }
}
