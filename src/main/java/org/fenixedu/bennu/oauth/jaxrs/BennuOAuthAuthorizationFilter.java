package org.fenixedu.bennu.oauth.jaxrs;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

class BennuOAuthAuthorizationFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest httpRequest;

    private final static String ACCESS_TOKEN = "access_token";

    private final static String USER_HEADER = "__username__";

    private static final Logger logger = LoggerFactory.getLogger(BennuOAuthAuthorizationFilter.class);

    private final OAuthEndpoint endpoint;

    public BennuOAuthAuthorizationFilter(Method method, OAuthEndpoint endpoint) {
        this.endpoint = endpoint;
        logger.debug("Securing REST endpoint {} with scope '{}'", method, endpoint.value());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String ipAddress = getIpAddress();

        String accessToken = getAccessToken(requestContext);

        Optional<ServiceApplication> serviceApplication = extractServiceApplication(accessToken);

        if (endpoint.serviceOnly() && !serviceApplication.isPresent()) {
            requestContext.abortWith(Response.status(Status.NOT_FOUND).build());
            return;
        }

        if (serviceApplication.isPresent()) {

            if (serviceApplication.get().isDeleted()) {
                sendError(requestContext, "accessTokenInvalidFormat", "Access Token not recognized.");
                return;
            }

            if (serviceApplication.get().isBanned()) {
                sendError(requestContext, "appBanned", "The application has been banned.");
                return;
            }

            if (!serviceApplication.get().hasServiceAuthorization(accessToken)) {
                requestContext.abortWith(Response.status(Status.NOT_FOUND).build());
                return;
            }

            if (!Strings.isNullOrEmpty(ipAddress) && !serviceApplication.get().matchesIpAddress(ipAddress)) {
                requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
                return;
            }

            String username = getUserParam(requestContext);

            if (!Strings.isNullOrEmpty(username)) {
                User user = User.findByUsername(username);
                if (user != null) {
                    Authenticate.mock(user);
                }
            }

            return;
        }

        if (Authenticate.isLogged()) {
            logger.trace("Already logged in, proceeding...");
            return;
        }

        Optional<ExternalApplicationScope> scope = ExternalApplicationScope.forKey(endpoint.value());

        if (scope.isPresent()) {
            Optional<ApplicationUserSession> session = extractUserSession(accessToken);
            if (session.isPresent()) {
                ApplicationUserSession appUserSession = session.get();
                ExternalApplication app = session.get().getApplicationUserAuthorization().getApplication();
                if (app.isDeleted()) {
                    sendError(requestContext, "accessTokenInvalidFormat", "Access Token not recognized.");
                    return;
                }

                if (app.isBanned()) {
                    sendError(requestContext, "appBanned", "The application has been banned.");
                    return;
                }

                if (!app.getScopesSet().contains(scope.get())) {
                    sendError(requestContext, "invalidScope", "Application doesn't have permissions to this endpoint.");
                    return;
                }

                if (!appUserSession.matchesAccessToken(accessToken)) {
                    sendError(requestContext, "accessTokenInvalid", "Access Token doesn't match.");
                    return;
                }

                if (!appUserSession.isAccessTokenValid()) {
                    sendError(requestContext, "accessTokenExpired",
                            "The access has expired. Please use the refresh token endpoint to generate a new one.");
                    return;
                }

                User foundUser = appUserSession.getApplicationUserAuthorization().getUser();
                Authenticate.mock(foundUser);
            } else {
                sendError(requestContext, "accessTokenInvalidFormat", "Access Token not recognized.");
                return;
            }

        } else {
            logger.debug("Scope '{}' is not defined!", endpoint.value());
            requestContext.abortWith(Response.status(Status.NOT_FOUND).build());
        }
    }

    private String getIpAddress() {

        String xFor = httpRequest.getHeader("x-forwarded-for");

        if (!Strings.isNullOrEmpty(xFor)) {
            return xFor;
        }

        return httpRequest.getRemoteAddr();
    }

    private void sendError(ContainerRequestContext requestContext, String error, String errorDescription) {
        JsonObject json = new JsonObject();
        json.addProperty("error", error);
        json.addProperty("error_description", errorDescription);
        requestContext.abortWith(Response.status(Status.UNAUTHORIZED).entity(json.toString()).type(MediaType.APPLICATION_JSON)
                .build());
    }

    private Optional<ServiceApplication> extractServiceApplication(String accessToken) {

        if (Strings.isNullOrEmpty(accessToken)) {
            return Optional.empty();
        }
        String fullToken = new String(Base64.getDecoder().decode(accessToken));
        String[] accessTokenBuilder = fullToken.split(":");
        if (accessTokenBuilder.length != 2) {
            return Optional.empty();
        }

        return getDomainObject(accessTokenBuilder[0], ServiceApplication.class);
    }

    private Optional<ApplicationUserSession> extractUserSession(String accessToken) {
        if (Strings.isNullOrEmpty(accessToken)) {
            return Optional.empty();
        }
        String fullToken = new String(Base64.getDecoder().decode(accessToken));
        String[] accessTokenBuilder = fullToken.split(":");
        if (accessTokenBuilder.length != 2) {
            return Optional.empty();
        }

        return getDomainObject(accessTokenBuilder[0], ApplicationUserSession.class);
    }

    private String getAccessToken(ContainerRequestContext requestContext) {
        return getHeaderOrQueryParam(requestContext, ACCESS_TOKEN);
    }

    private String getUserParam(ContainerRequestContext requestContext) {
        return getHeaderOrQueryParam(requestContext, USER_HEADER);
    }

    private String getHeaderOrQueryParam(ContainerRequestContext requestContext, String paramName) {
        String paramValue = requestContext.getHeaderString(paramName);
        if (Strings.isNullOrEmpty(paramValue)) {
            paramValue = requestContext.getUriInfo().getQueryParameters().getFirst(paramName);
        }
        return paramValue;
    }

    public static final <T extends DomainObject> Optional<T> getDomainObject(final String externalId, final Class<T> clazz) {
        try {
            T domainObject = FenixFramework.getDomainObject(externalId);
            if (!FenixFramework.isDomainObjectValid(domainObject) || !clazz.isAssignableFrom(domainObject.getClass())) {
                return Optional.empty();
            }
            return Optional.of(domainObject);
        } catch (Exception nfe) {
            return Optional.empty();
        }
    }

}
