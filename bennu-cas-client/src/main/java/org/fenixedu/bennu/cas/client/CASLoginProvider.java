package org.fenixedu.bennu.cas.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class CASLoginProvider implements LoginProvider {

    private final Escaper escaper = UrlEscapers.urlPathSegmentEscaper();

    @Override
    public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback) throws IOException,
            ServletException {
        if (Strings.isNullOrEmpty(callback)) {
            callback = CASClientConfiguration.getConfiguration().casServiceUrl();
        }
        callback = Base64.getUrlEncoder().encodeToString(callback.getBytes(StandardCharsets.UTF_8));
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
}
