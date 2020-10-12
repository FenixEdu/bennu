package org.fenixedu.bennu.saml.client;

import com.onelogin.saml2.Auth;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SAMLLoginProvider implements LoginProvider {

    private static final Logger logger = LoggerFactory.getLogger(SAMLLoginProvider.class);
    private static final String APP_URL = CoreConfiguration.getConfiguration().applicationUrl();
    private static final String CALLBACK_URL = APP_URL + "/api/saml-client/returnFromSAML";

    @Override
    public void showLogin(final HttpServletRequest request, final HttpServletResponse response, String callback) {
        Authenticate.logout(request, response);

        try {
            Auth auth = SAMLClientSDK.getAuth(request, response);
            auth.login(CALLBACK_URL);
        } catch (Exception e) {
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
