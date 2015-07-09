/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth.
 *
 * Bennu OAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.oauth.jaxrs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
import org.fenixedu.bennu.oauth.util.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

class BennuOAuthAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(BennuOAuthAuthorizationFilter.class);

    @Context
    ResourceInfo requestInfo;

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String ipAddress = getIpAddress();

        String accessToken = getAccessToken(requestContext);

        final OAuthEndpoint endpoint = requestInfo.getResourceMethod().getAnnotation(OAuthEndpoint.class);

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

            Optional<ExternalApplicationScope> scope = ExternalApplicationScope.forKey(endpoint.value());

            if (scope.isPresent() && !serviceApplication.get().getScopesSet().contains(scope.get())) {
                sendError(requestContext, "invalidScope", "Application doesn't have permissions to this endpoint.");
                return;
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
                    sendError(requestContext, "invalidScope", "Application doesn't have permissions to this getEndpoint().");
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

                if (foundUser.isLoginExpired()) {
                    sendError(requestContext, "accessTokenInvalidFormat", "Access Token not recognized.");
                    return;
                }

                Authenticate.mock(foundUser);
            } else {
                sendError(requestContext, "accessTokenInvalidFormat", "Access Token not recognized.");
                return;
            }

        } else {
            logger.debug("Scope '{}' is not defined!", (Object) endpoint.value());
            requestContext.abortWith(Response.status(Status.NOT_FOUND).build());
        }
    }

    private String getIpAddress() {

        if (httpRequest != null) {
            String xFor = null;
            xFor = httpRequest.getHeader("x-forwarded-for");

            if (!Strings.isNullOrEmpty(xFor)) {
                return xFor;
            }

            return httpRequest.getRemoteAddr();
        }

        return null;
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

        try {
            String fullToken = new String(Base64.getDecoder().decode(accessToken), StandardCharsets.UTF_8);
            String[] accessTokenBuilder = fullToken.split(":");
            if (accessTokenBuilder.length != 2) {
                return Optional.empty();
            }
            return OAuthUtils.getDomainObject(accessTokenBuilder[0], ServiceApplication.class);
        } catch (IllegalArgumentException iea) {
            return Optional.empty();
        }
    }

    private Optional<ApplicationUserSession> extractUserSession(String accessToken) {
        if (Strings.isNullOrEmpty(accessToken)) {
            return Optional.empty();
        }
        try {
            String fullToken = new String(Base64.getDecoder().decode(accessToken), StandardCharsets.UTF_8);
            String[] accessTokenBuilder = fullToken.split(":");
            if (accessTokenBuilder.length != 2) {
                return Optional.empty();
            }

            return OAuthUtils.getDomainObject(accessTokenBuilder[0], ApplicationUserSession.class);
        } catch (IllegalArgumentException iea) {
            return Optional.empty();
        }

    }

    private String getAccessToken(ContainerRequestContext requestContext) {
        return getHeaderOrQueryParam(requestContext, OAuthUtils.ACCESS_TOKEN);
    }

    private String getAuthorizationHeader(ContainerRequestContext request) {
        String authorization = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(OAuthUtils.TOKEN_TYPE_HEADER_ACCESS_TOKEN)) {
            return authorization.substring(OAuthUtils.TOKEN_TYPE_HEADER_ACCESS_TOKEN.length()).trim();
        }
        return null;
    }

    private String getHeaderOrQueryParam(ContainerRequestContext requestContext, String paramName) {
        String paramValue = getAuthorizationHeader(requestContext);
        if (!Strings.isNullOrEmpty(paramValue)) {
            return paramValue;
        }
        paramValue = requestContext.getHeaderString(paramName);
        if (Strings.isNullOrEmpty(paramValue)) {
            paramValue = requestContext.getUriInfo().getQueryParameters().getFirst(paramName);
        }
        return paramValue;
    }
}
