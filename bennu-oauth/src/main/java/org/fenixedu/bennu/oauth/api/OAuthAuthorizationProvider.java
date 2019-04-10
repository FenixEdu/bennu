package org.fenixedu.bennu.oauth.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.fenixedu.bennu.oauth.util.OAuthUtils;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonElement;

@Path("/oauth")
public class OAuthAuthorizationProvider extends BennuRestResource {

    /***
     * Obtains (creating if necessary) oauth tokens for an {@link ExternalApplication} on behalf of a {@link User}.
     * 
     * It will create an {@link ApplicationUserAuthorization} (if it does not exist yet) as well as the corresponding
     * {@link ApplicationUserSession}.
     * 
     * This should be used when a client {@link ServiceApplication} needs to access {@link OAuthEndpoint} endpoints on behalf of
     * specific {@link User}. Usually this happens in a scenario where both applications (oauth client and oauth server) live
     * in the same ecosystem.
     * 
     * @param app the application grant access to
     * @param username the username of the user to generate tokens to
     * @return oauth tokens granted for app on behalf of user
     */
    @POST
    @Path("/provider/{app}/{username}")
    @OAuthEndpoint(serviceOnly = true, value = "OAUTH_AUTHORIZATION_PROVIDER")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement getTokens(@PathParam("app") ExternalApplication app, @PathParam("username") String username) {
        User user = User.findByUsername(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return OAuthUtils.getJsonTokens(getOrCreateApplicationUserSession(app, user));
    }

    @Atomic
    private ApplicationUserSession getOrCreateApplicationUserSession(ExternalApplication app, User user) {
        ApplicationUserAuthorization applicationUserAuthorization = app.getApplicationUserAuthorization(user).orElseGet(() -> {
            return new ApplicationUserAuthorization(user, app);
        });
        ApplicationUserSession applicationUserSession =
                applicationUserAuthorization.getSessionSet().stream()
                        .filter(ApplicationUserSession::isAccessTokenValid)
                        .findAny()
                        .orElseGet(() -> {
                            final ApplicationUserSession session = new ApplicationUserSession();
                            session.setApplicationUserAuthorization(applicationUserAuthorization);
                            session.setTokens(OAuthUtils.generateToken(session), OAuthUtils.generateToken(session));
                            return session;
                        });
        return applicationUserSession;
    }
}
