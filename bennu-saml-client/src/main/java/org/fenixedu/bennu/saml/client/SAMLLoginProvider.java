package org.fenixedu.bennu.saml.client;

import com.google.common.base.Strings;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.saml.client.SAML2Client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SAMLLoginProvider implements LoginProvider {

    @Override
    public void showLogin(final HttpServletRequest request, final HttpServletResponse response, final String callback) {
        Authenticate.logout(request, response);
        final JEEContext context = new JEEContext(request, response);

        final SAML2Client client = SAMLClientSDK.getClient();

        try {
            final HttpAction action = client.getRedirectionAction(context).get();
            JEEHttpActionAdapter.INSTANCE.adapt(action, context);
        } catch (final HttpAction ex) {
            throw new Error(ex);
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
