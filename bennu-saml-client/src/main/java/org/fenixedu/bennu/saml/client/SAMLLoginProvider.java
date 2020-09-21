package org.fenixedu.bennu.saml.client;

import com.onelogin.saml2.Auth;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SAMLLoginProvider implements LoginProvider {

    private static final Logger logger = LoggerFactory.getLogger(SAMLLoginProvider.class);

    @Override
    public void showLogin(final HttpServletRequest request, final HttpServletResponse response, String callback)
            throws IOException {
//        Authenticate.logout(request, response);

        try {
            callback = Base64.getUrlEncoder().encodeToString("xxxxxxxxx".getBytes(StandardCharsets.UTF_8));
            Auth auth = SAMLClientSDK.getAuth(request, response);
            auth.login(callback);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage(), e);
            // return Response.status(Response.Status.BAD_REQUEST).location(new URI("")).build();
        }
    }

    @Override
    public String getKey() {
        return "saml";
    }

    @Override
    public String getName() {
        return "SAML";
    }

    @Override
    public boolean isEnabled() {
        return SAMLClientConfiguration.getConfiguration().samlEnabled();
    }
}
