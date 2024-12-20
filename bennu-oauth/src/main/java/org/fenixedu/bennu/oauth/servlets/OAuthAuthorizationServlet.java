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
package org.fenixedu.bennu.oauth.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.OAuthProperties;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.fenixedu.bennu.oauth.domain.UserPKCEInfo;
import org.fenixedu.bennu.oauth.util.OAuthUtils;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

/**
 * Servlet implementation class OAuthAuthorizationServlet
 */
@WebServlet("/oauth/*")
public class OAuthAuthorizationServlet extends HttpServlet {

    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private static final String CODE_EXPIRED = "code expired";

    private static final String CODE_INVALID = "code invalid";

    private static final long serialVersionUID = 1L;

    private final static String OAUTH_SESSION_KEY = "OAUTH_CLIENT_ID";

    private final static String CLIENT_ID = "client_id";
    private final static String CLIENT_SECRET = "client_secret";
    private final static String REDIRECT_URI = "redirect_uri";
    private final static String CODE = "code";
    private final static String CSRF_HEADER = "X-CSRF-Token";

    private final static String ACCESS_TOKEN = "access_token";
    private final static String REFRESH_TOKEN = "refresh_token";
    private final static String GRANT_TYPE = "grant_type";
    private final static String DEVICE_ID = "device_id";
    private final static String STATE = "state";
    private final static String EXPIRES_IN = "expires_in";
    private final static String TOKEN_TYPE = "token_type";
    private final static String TOKEN_TYPE_VALUE = "Bearer";
    private final static String CODE_CHALLENGE = "code_challenge";
    private final static String CODE_CHALLENGE_METHOD = "code_challenge_method";
    private final static String CODE_CHALLENGE_METHOD_SUPORTED = "S256";
    private final static String CODE_VERIFIER = "code_verifier";
    private final static String RESPONSE_TYPE = "response_type";

    private final static String INVALID_GRANT = "invalid_grant";
    private static final String REFRESH_TOKEN_DOESN_T_MATCH = "refresh token doesn't match";
    private static final String CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH = "credentials or redirect_uri don't match";
    private static final String REFRESH_TOKEN_NOT_RECOGNIZED = "refresh token not recognized.";
    private static final String REFRESH_TOKEN_INVALID = "refreshTokenInvalid";
    private static final String REFRESH_TOKEN_INVALID_FORMAT = "refreshTokenInvalidFormat";
    private static final String CLIENT_ID_NOT_FOUND = "client_id not found";
    private static final String APPLICATION_BANNED = "the application has been banned.";
    private static final String APPLICATION_DELETED = "the application has been deleted.";
    private static final String NO_CSRF_HEADER = "The request don't has a CSRF header.";
    private static final String NO_CSRF_HEADER_DESCRIPTION = "To make this request the browser need to send a CSRF header.";

    private PebbleEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        engine = new Builder().loader(new ClasspathLoader() {
            @Override
            public Reader getReader(String pageName) throws LoaderException {
                InputStream stream = config.getServletContext().getResourceAsStream(
                        "/themes/" + PortalConfiguration.getInstance().getTheme() + "/oauth/" + pageName + ".html");
                if (stream != null) {
                    return new InputStreamReader(stream, StandardCharsets.UTF_8);
                } else {
                    // ... and fall back if none is provided.
                    return new InputStreamReader(
                            config.getServletContext().getResourceAsStream("/bennu-oauth/" + pageName + ".html"),
                            StandardCharsets.UTF_8);
                }
            }
        }).cacheActive(!BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()).build();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Strings.isNullOrEmpty(request.getPathInfo())) {
            response.sendError(404);
            return;
        }
        String path = trim(request.getPathInfo());

        // Allow OAuth API to be called from anywhere
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        switch (path) {
        case OAuthUtils.USER_DIALOG:
        case OAuthUtils.STANDARD_USER_DIALOG:
            handleUserDialog(request, response, path);
            break;
        case OAuthUtils.USER_CONFIRMATION:
            if (!"POST".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            userConfirmation(request, response);
            break;
        case OAuthUtils.ACCESS_TOKEN:
        case OAuthUtils.STANDARD_ACCESS_TOKEN:
            if("OPTIONS".equals(request.getMethod())) {
                response.setHeader("Access-Control-Allow-Methods", "POST");
                return;
            }

            if (!"POST".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            handleAccessToken(request, response);
            break;
        case OAuthUtils.REFRESH_TOKEN:
            if (!"POST".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            handleRefreshToken(request, response);
            break;

        default:
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // refreshAccessToken
    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String[] authorizationHeader = getAuthorizationHeader(request);

        String clientId;
        String clientSecret;

        if (authorizationHeader == null) {
            clientId = request.getParameter(CLIENT_ID);
            clientSecret = request.getParameter(CLIENT_SECRET);
        } else {
            clientId = authorizationHeader[0];
            clientSecret = authorizationHeader[1];
        }

        String refreshToken = request.getParameter(OAuthUtils.REFRESH_TOKEN);

        ExternalApplication externalApplication = OAuthUtils.getDomainObject(clientId, ExternalApplication.class).orElse(null);

        if (externalApplication == null) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (!isValidApplication(response, externalApplication)) { // this method sends error response if needed
            return;
        }

        if (Strings.isNullOrEmpty(refreshToken)) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, REFRESH_TOKEN_INVALID_FORMAT, REFRESH_TOKEN_NOT_RECOGNIZED);
            return;
        }

        String refreshTokenDecoded;
        try {
            refreshTokenDecoded = new String(Base64.getDecoder().decode(refreshToken), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException iae) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, REFRESH_TOKEN_INVALID_FORMAT, REFRESH_TOKEN_NOT_RECOGNIZED);
            return;
        }

        String[] refreshTokenBuilder = refreshTokenDecoded.split(":");

        if (refreshTokenBuilder.length != 2) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, REFRESH_TOKEN_INVALID_FORMAT, REFRESH_TOKEN_NOT_RECOGNIZED);
            return;
        }

        String appUserSessionExternalId = refreshTokenBuilder[0];

        ApplicationUserSession appUserSession = FenixFramework.getDomainObject(appUserSessionExternalId);

        if (!externalApplication.matchesSecret(clientSecret) && appUserSession.getUserPKCEInfoAuthorizationSession() == null) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (!FenixFramework.isDomainObjectValid(appUserSession) || !appUserSession.matchesRefreshToken(refreshToken)) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, REFRESH_TOKEN_INVALID, REFRESH_TOKEN_DOESN_T_MATCH);
            return;
        }

        if (appUserSession.getApplicationUserAuthorization().getUser().isLoginExpired()) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, REFRESH_TOKEN_INVALID_FORMAT, REFRESH_TOKEN_NOT_RECOGNIZED);
            return;
        }

        String newAccessToken = OAuthUtils.generateToken(appUserSession);
        appUserSession.setNewAccessToken(newAccessToken);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ACCESS_TOKEN, newAccessToken);
        jsonResponse.addProperty(REFRESH_TOKEN, refreshToken);
        jsonResponse.addProperty(TOKEN_TYPE, TOKEN_TYPE_VALUE);
        jsonResponse.addProperty(EXPIRES_IN, OAuthProperties.getConfiguration().getAccessTokenExpirationSeconds());
        sendOAuthResponse(response, Status.OK, jsonResponse);
    }

    private String[] getAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String[] values;
            try {
                String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
                values = credentials.split(":", 2);
            } catch (IllegalArgumentException iae) {
                return null;
            }
            return values.length != 2 ? null : values;
        }

        return null;
    }

    // getTokens
    private void handleAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String[] authorizationHeader = getAuthorizationHeader(request);

        String clientId;
        String clientSecret;

        if (authorizationHeader == null) {
            clientId = request.getParameter(CLIENT_ID);
            clientSecret = request.getParameter(CLIENT_SECRET);
        } else {
            clientId = authorizationHeader[0];
            clientSecret = authorizationHeader[1];
        }

        String redirectUrl = request.getParameter(REDIRECT_URI);
        String authCode = request.getParameter(CODE);
        String grantType = request.getParameter(GRANT_TYPE);
        String codeVerifier = request.getParameter(CODE_VERIFIER);

        if (Strings.isNullOrEmpty(clientId) || (Strings.isNullOrEmpty(clientSecret) && Strings.isNullOrEmpty(codeVerifier))
                || Strings.isNullOrEmpty(grantType)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT,
                    Joiner.on(",").join(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE) + " are mandatory");
            return;
        }

        if (!GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType) && !GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT,
                    GRANT_TYPE + " must be on of the following values: "
                            + Joiner.on(",").join(GRANT_TYPE_CLIENT_CREDENTIALS, GRANT_TYPE_AUTHORIZATION_CODE));
            return;
        }

        if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
            if (Strings.isNullOrEmpty(redirectUrl) || Strings.isNullOrEmpty(authCode)) {
                sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT,
                        Joiner.on(",").join(REDIRECT_URI, CODE) + " are mandatory");
                return;
            }
        }

        ExternalApplication externalApplication = OAuthUtils.getDomainObject(clientId, ExternalApplication.class).orElse(null);

        if (externalApplication == null) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (externalApplication instanceof ServiceApplication && !GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (!isValidApplication(response, externalApplication)) { // this method sends error response if needed
            return;
        }

        if (!Strings.isNullOrEmpty(clientSecret) && !externalApplication.matches(redirectUrl, clientSecret)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (!Strings.isNullOrEmpty(codeVerifier) && !externalApplication.matchesPKCE(redirectUrl, codeVerifier, authCode)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (externalApplication instanceof ServiceApplication) {
            final String accessToken = OAuthUtils.generateToken(externalApplication);
            ((ServiceApplication) externalApplication).createServiceAuthorization(accessToken);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty(ACCESS_TOKEN, accessToken);
            sendOAuthResponse(response, Status.OK, jsonResponse);
            return;
        }

        ApplicationUserSession appUserSession = externalApplication.getApplicationUserSession(authCode);

        if (appUserSession == null) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CODE_INVALID);
            return;
        }

        if (appUserSession.getApplicationUserAuthorization().getUser().isLoginExpired()) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CODE_EXPIRED);
            return;
        }

        if (appUserSession.isCodeValid()) {
            String accessToken = OAuthUtils.generateToken(appUserSession);
            String refreshToken = OAuthUtils.generateToken(appUserSession);
            appUserSession.setTokens(accessToken, refreshToken);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty(ACCESS_TOKEN, accessToken);
            jsonResponse.addProperty(REFRESH_TOKEN, refreshToken);
            jsonResponse.addProperty(TOKEN_TYPE, TOKEN_TYPE_VALUE);
            jsonResponse.addProperty(EXPIRES_IN, OAuthProperties.getConfiguration().getAccessTokenExpirationSeconds());
            sendOAuthResponse(response, Status.OK, jsonResponse);
        } else {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CODE_EXPIRED);
        }

    }

    private void handleUserDialog(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        String clientId = request.getParameter(CLIENT_ID);
        String redirectUrl = request.getParameter(REDIRECT_URI);
        String originalState = request.getParameter(STATE);
        String codeChallenge = request.getParameter(CODE_CHALLENGE);
        String codeChallengeMethod = request.getParameter(CODE_CHALLENGE_METHOD);
        String responseType = request.getParameter(RESPONSE_TYPE);
        User user = Authenticate.getUser();
        if (!Strings.isNullOrEmpty(codeChallenge) && !Strings.isNullOrEmpty(codeChallengeMethod)
                && !Strings.isNullOrEmpty(responseType)) {
            // With code_chanllenge, code_verifier, code_chanllenge_method and redirectUrl

            // This confirmation should not be used only in the pkce flow, but for legacy
            // will only be used in pkce
            if (!responseType.equals(CODE)) {
                errorPage(request, response);
                return;
            }

            if (Strings.isNullOrEmpty(clientId) || Strings.isNullOrEmpty(redirectUrl)) {
                errorPage(request, response);
                return;
            }

            // Only sha256 support, "plain" not available
            if (!codeChallengeMethod.equals(CODE_CHALLENGE_METHOD_SUPORTED)) {
                errorPage(request, response);
                return;
            }

            if (user == null) {
                String cookieValue = clientId + "|" + redirectUrl + "|" + codeChallenge + "|" + codeChallengeMethod;
                if (originalState != null) {
                    cookieValue += "|" + Base64.getEncoder().encodeToString(originalState.getBytes(StandardCharsets.UTF_8));
                }
                response.addCookie(new Cookie(OAUTH_SESSION_KEY,
                        Base64.getEncoder().encodeToString(cookieValue.getBytes(StandardCharsets.UTF_8))));
                response.sendRedirect(request.getContextPath() + "/login?callback="
                        + CoreConfiguration.getConfiguration().applicationUrl() + "/oauth/" + path);
                return;
            } else {
                redirectToRedirectUrlPKCE(request, response, user, clientId, redirectUrl, originalState, codeChallenge,
                        codeChallengeMethod);
                return;
            }

        } else if (!Strings.isNullOrEmpty(clientId) && !Strings.isNullOrEmpty(redirectUrl)) {
            // With clientId and redirectUrl
            if (user == null) {
                String cookieValue = clientId + "|" + redirectUrl;
                if (originalState != null) {
                    cookieValue += "|" + Base64.getEncoder().encodeToString(originalState.getBytes(StandardCharsets.UTF_8));
                }
                response.addCookie(new Cookie(OAUTH_SESSION_KEY,
                        Base64.getEncoder().encodeToString(cookieValue.getBytes(StandardCharsets.UTF_8))));
                response.sendRedirect(request.getContextPath() + "/login?callback="
                        + CoreConfiguration.getConfiguration().applicationUrl() + "/oauth/" + path);
                return;
            } else {
                redirectToRedirectUrl(request, response, user, clientId, redirectUrl, originalState);
                return;
            }
        } else {
            // User has oauth cookie
            if (user != null) {
                final Cookie cookie = getOAuthSessionCookie(request);
                if (cookie == null) {
                    errorPage(request, response);
                    return;
                }
                final String sessionClientId = cookie.getValue();
                if (!Strings.isNullOrEmpty(sessionClientId)) {
                    redirectToRedirectUrl(request, response, user, cookie);
                    return;
                }
            }
        }
        errorPage(request, response);
    }

    private static Cookie getOAuthSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(OAUTH_SESSION_KEY)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private void errorPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> ctx = new HashMap<>();
        PortalConfiguration config = PortalConfiguration.getInstance();
        // Add relevant variables
        ctx.put("config", config);
        ctx.put("currentLocale", I18N.getLocale());
        ctx.put("contextPath", request.getContextPath());
        ctx.put("locales", CoreConfiguration.supportedLocales());

        try {
            response.setContentType("text/html;charset=UTF-8");
            PebbleTemplate template = engine.getTemplate("error-page");
            template.evaluate(response.getWriter(), ctx, I18N.getLocale());
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }

    private void authorizationPage(HttpServletRequest request, HttpServletResponse response,
            ExternalApplication clientApplication, String redirectUrl, String state) throws IOException {
        Map<String, Object> ctx = new HashMap<>();
        PortalConfiguration config = PortalConfiguration.getInstance();
        // Add relevant variables
        ctx.put("config", config);
        ctx.put("app", clientApplication);
        ctx.put("currentLocale", I18N.getLocale());
        ctx.put("contextPath", request.getContextPath());
        ctx.put("locales", CoreConfiguration.supportedLocales());
        ctx.put("loggedUser", Authenticate.getUser());
        ctx.put("state", state);
        ctx.put("redirectUrl", redirectUrl);
        try {
            response.setContentType("text/html;charset=UTF-8");
            PebbleTemplate template = engine.getTemplate("auth-page");
            template.evaluate(response.getWriter(), ctx, I18N.getLocale());
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }

    private void redirectToRedirectUrl(HttpServletRequest request, HttpServletResponse response, User user, final Cookie cookie)
            throws IOException {
        String cookieValue = new String(Base64.getDecoder().decode(cookie.getValue()));
        String[] values = cookieValue.split("\\|");
        String clientApplicationId = values[0];
        String redirectUrl = values[1];
        String state = null;
        String codeChallenge = null;
        String codeChallengeMethod = null;
        if (values.length == 3 && !Strings.isNullOrEmpty(values[2])) {
            state = new String(Base64.getDecoder().decode(values[2]));
            redirectToRedirectUrl(request, response, user, clientApplicationId, redirectUrl, state);
        } else if (values.length == 4 && !Strings.isNullOrEmpty(values[2]) && !Strings.isNullOrEmpty(values[3])) {
            codeChallenge = new String(values[2]);
            codeChallengeMethod = new String(values[3]);
            redirectToRedirectUrlPKCE(request, response, user, clientApplicationId, redirectUrl, state, codeChallenge, codeChallengeMethod);
        } else if (values.length == 5 && !Strings.isNullOrEmpty(values[2]) && !Strings.isNullOrEmpty(values[3])
                && !Strings.isNullOrEmpty(values[4])) {
            codeChallenge = new String(values[2]);
            codeChallengeMethod = new String(values[3]);
            state = new String(Base64.getDecoder().decode(values[4]));
            redirectToRedirectUrlPKCE(request, response, user, clientApplicationId, redirectUrl, state, codeChallenge, codeChallengeMethod);

        } else {
            redirectToRedirectUrl(request, response, user, clientApplicationId, redirectUrl, state);
        }
    }

    private void redirectToRedirectUrl(HttpServletRequest request, HttpServletResponse response, User user, String clientId,
            String redirectUrl, String state) throws IOException {

        ExternalApplication externalApplication = OAuthUtils.getDomainObject(clientId, ExternalApplication.class).orElse(null);

        if (externalApplication == null || externalApplication instanceof ServiceApplication) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (!isValidApplication(response, externalApplication)) { // this method sends error response if needed
            return;
        }

        if (!externalApplication.matchesUrl(redirectUrl)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (!externalApplication.hasApplicationUserAuthorization(user)) {
            request.setAttribute("application", externalApplication);
            authorizationPage(request, response, externalApplication, redirectUrl, state);
            return;
        } else {
            redirectWithCode(request, response, user, externalApplication, redirectUrl, state);
            return;
        }

    }

    private void redirectToRedirectUrlPKCE(HttpServletRequest request, HttpServletResponse response, User user, String clientId,
            String redirectUrl, String state, String codeChallenge, String codeChallengeMethod) throws IOException {

        ExternalApplication externalApplication = OAuthUtils.getDomainObject(clientId, ExternalApplication.class).orElse(null);

        if (externalApplication == null || externalApplication instanceof ServiceApplication) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (!isValidApplication(response, externalApplication)) { // this method sends error response if needed
            return;
        }

        if (!externalApplication.matchesUrl(redirectUrl)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (!externalApplication.hasApplicationUserAuthorization(user)) {
            request.setAttribute("application", externalApplication);
            HttpSession session = request.getSession();
            session.setAttribute(CODE_CHALLENGE, codeChallenge);
            authorizationPage(request, response, externalApplication, redirectUrl, state);
            return;
        } else {
            UserPKCEInfo userPKCEInfo = saveCodeChallenge(user, externalApplication, codeChallenge);
            redirectWithCodePKCE(request, response, user, externalApplication, redirectUrl, state, userPKCEInfo);
            return;
        }
    }

    @Atomic
    private UserPKCEInfo saveCodeChallenge(User user, ExternalApplication externalApplication, String codeChallenge) {
        return new UserPKCEInfo(user, externalApplication, codeChallenge);
    }

    private void redirectWithCodePKCE(HttpServletRequest request, HttpServletResponse response, User user,
            ExternalApplication clientApplication, String redirectUrl, String state, UserPKCEInfo userPKCEInfo)
            throws IOException {

        if (!clientApplication.hasCodeSecret()) {
            clientApplication.generateCodeSecret();
        }
        final String code = createAppUserSessionPKCE(clientApplication, user, request, response, userPKCEInfo);
        UriBuilder builder = UriBuilder.fromUri(redirectUrl);
        builder.queryParam(CODE, code);
        if (!Strings.isNullOrEmpty(state)) {
            builder.queryParam(STATE, state);
        }
        response.sendRedirect(builder.toString());
    }

    private void redirectWithCode(HttpServletRequest request, HttpServletResponse response, User user,
            ExternalApplication clientApplication, String redirectUrl, String state) throws IOException {
        if (!clientApplication.hasCodeSecret()) {
            clientApplication.generateCodeSecret();
        }

        final String code = createAppUserSession(clientApplication, user, request, response);
        UriBuilder builder = UriBuilder.fromUri(redirectUrl);
        builder.queryParam(CODE, code);
        if (!Strings.isNullOrEmpty(state)) {
            builder.queryParam(STATE, state);
        }
        response.sendRedirect(builder.toString());
    }

    public void userConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = Authenticate.getUser();
        if (user == null) {
            errorPage(request, response);
            return;
        }

        String clientId = request.getParameter(CLIENT_ID);
        String redirectUrl = request.getParameter(REDIRECT_URI);
        String state = request.getParameter(STATE);
        String csrfToken = request.getHeader(CSRF_HEADER);

        // Only a website in the same origin can send custom headers, unless the contrary is specified in response cors
        // header received in an preflight request using an OPTIONS request
        if (csrfToken == null){
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, NO_CSRF_HEADER, NO_CSRF_HEADER_DESCRIPTION);
            return;
        }

        HttpSession session = request.getSession();
        String codeChallenge = (String) session.getAttribute(CODE_CHALLENGE);

        ExternalApplication externalApplication = (ExternalApplication) OAuthUtils.getDomainObject(clientId).orElse(null);

        if (externalApplication == null || externalApplication instanceof ServiceApplication) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (!isValidApplication(response, externalApplication)) { // this method sends error response if needed
            return;
        }

        if (externalApplication.matchesUrl(redirectUrl)) {
            if (!Strings.isNullOrEmpty(codeChallenge)) {
                UserPKCEInfo userPKCEInfo = saveCodeChallenge(user, externalApplication, codeChallenge);
                redirectWithCodePKCE(request, response, user, externalApplication, redirectUrl, state, userPKCEInfo);
            } else {
                redirectWithCode(request, response, user, externalApplication, redirectUrl, state);
            }
            return;
        }

        errorPage(request, response);
    }

    private boolean isValidApplication(HttpServletResponse response, ExternalApplication clientApplication) {

        if (clientApplication.isDeleted()) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, INVALID_GRANT, APPLICATION_DELETED);
            return false;
        }

        if (clientApplication.isBanned()) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, INVALID_GRANT, APPLICATION_BANNED);
            return false;
        }
        return true;
    }

    @Atomic
    private static String createAppUserSessionPKCE(ExternalApplication application, User user, HttpServletRequest request,
            HttpServletResponse response, UserPKCEInfo userPKCEInfo) {
        String code = OAuthUtils.generateCode(user.getUsername(), application.getCodeSecret());
        ApplicationUserAuthorization appUserAuthorization = application.getApplicationUserAuthorization(user)
                .orElseGet(() -> new ApplicationUserAuthorization(user, application));
        ApplicationUserSession appUserSession = new ApplicationUserSession();
        appUserSession.setCode(code);
        appUserSession.setDeviceId(getDeviceId(request));
        appUserSession.setApplicationUserAuthorization(appUserAuthorization);
        appUserSession.setUserPKCEInfoAuthorizationSession(userPKCEInfo);
        return code;
    }

    @Atomic
    private static String createAppUserSession(ExternalApplication application, User user, HttpServletRequest request,
            HttpServletResponse response) {
        String code = OAuthUtils.generateCode(user.getUsername(), application.getCodeSecret());
        ApplicationUserAuthorization appUserAuthorization = application.getApplicationUserAuthorization(user)
                .orElseGet(() -> new ApplicationUserAuthorization(user, application));
        ApplicationUserSession appUserSession = new ApplicationUserSession();
        appUserSession.setCode(code);
        appUserSession.setDeviceId(getDeviceId(request));
        appUserSession.setApplicationUserAuthorization(appUserAuthorization);
        return code;
    }

    private void sendOAuthErrorResponse(HttpServletResponse response, Status status, String error, String errorDescription) {
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("error", error);
        errorResponse.addProperty("errorDescription", errorDescription);
        sendOAuthResponse(response, status, errorResponse);
    }

    private void sendOAuthResponse(HttpServletResponse response, Status status, JsonObject jsonResponse) {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(status.getStatusCode());
        try (PrintWriter pw = response.getWriter()) {
            pw.print(jsonResponse.toString());
            pw.flush();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    private static String getDeviceId(HttpServletRequest request) {
        String deviceId = request.getParameter(DEVICE_ID);
        if (Strings.isNullOrEmpty(deviceId)) {
            return request.getHeader(HttpHeaders.USER_AGENT);
        }
        return deviceId;
    }

    private String trim(String value) {
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();

        while ((st < len) && (val[st] == '/')) {
            st++;
        }
        while ((st < len) && (val[len - 1] == '/')) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

}
