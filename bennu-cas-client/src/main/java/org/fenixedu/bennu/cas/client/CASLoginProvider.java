package org.fenixedu.bennu.cas.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.cas.client.strategy.DefaultTicketValidationStrategy;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class CASLoginProvider implements LoginProvider {

    private final Escaper escaper = UrlEscapers.urlPathSegmentEscaper();

    @Override
    public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback)
            throws IOException, ServletException {

        // Although we receive the callback here in the API. The LoginRedirector that OMNIS.cloud platform
        // uses, need that the users always go through /startPage first, which is the defined casServiceURL 
        // that way we are ignoring the callback, so things can work the same way as the local and saml logins
        // already work.
        //
        //
        // 3 February 2023 - Paulo Abrantes
        //
        // The actual callback is store in session where the LoginRedirector expects it to be
        //
        // 11 July 2023 - Paulo Abrantes
        if (!StringUtils.isEmpty(callback)) {
            request.getSession(false).setAttribute(DefaultTicketValidationStrategy.CALLBACK_URL, callback);
        }
        String actualCallbackToUse = CASClientConfiguration.getConfiguration().casServiceUrl();
        actualCallbackToUse = Base64.getUrlEncoder().encodeToString(actualCallbackToUse.getBytes(StandardCharsets.UTF_8));
        response.sendRedirect(CASClientConfiguration.getConfiguration().casServerUrl() + "/login?service=" + escaper
                .escape(CoreConfiguration.getConfiguration().applicationUrl() + "/api/cas-client/login/" + actualCallbackToUse));
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
