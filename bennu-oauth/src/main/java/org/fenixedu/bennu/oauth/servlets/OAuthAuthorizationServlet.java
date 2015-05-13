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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CookieReaderUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.OAuthProperties;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Servlet implementation class OAuthAuthorizationServlet
 */
@WebServlet("/oauth/*")
public class OAuthAuthorizationServlet extends HttpServlet {

    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private static final String GRANT_TYPE_ACCESS_TOKEN = "access_token";

    private static final String CODE_EXPIRED = "code expired";

    private static final String CODE_INVALID = "code invalid";

    Logger logger = LoggerFactory.getLogger(OAuthAuthorizationServlet.class);

    private static final long serialVersionUID = 1L;

    private final static String OAUTH_SESSION_KEY = "OAUTH_CLIENT_ID";

    private final static String CLIENT_ID = "client_id";
    private final static String CLIENT_SECRET = "client_secret";
    private final static String REDIRECT_URI = "redirect_uri";
    private final static String CODE = "code";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String REFRESH_TOKEN = "refresh_token";
    private final static String GRANT_TYPE = "grant_type";
    private final static String EXPIRES_IN = "expires_in";
    private final static String DEVICE_ID = "device_id";

    private final static String INVALID_GRANT = "invalid_grant";
    private static final String REFRESH_TOKEN_DOESN_T_MATCH = "refresh token doesn't match";
    private static final String CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH = "credentials or redirect_uri don't match";
    private static final String REFRESH_TOKEN_NOT_RECOGNIZED = "refresh token not recognized.";
    private static final String REFRESH_TOKEN_INVALID = "refreshTokenInvalid";
    private static final String REFRESH_TOKEN_INVALID_FORMAT = "refreshTokenInvalidFormat";
    private static final String CLIENT_ID_NOT_FOUND = "client_id not found";
    private static final String APPLICATION_BANNED = "the application has been banned.";
    private static final String APPLICATION_DELETED = "the application has been deleted.";

    private PebbleEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        engine = new PebbleEngine(new ClasspathLoader() {
            @Override
            public Reader getReader(String pageName) throws LoaderException {
                InputStream stream =
                        config.getServletContext().getResourceAsStream(
                                "/themes/" + PortalConfiguration.getInstance().getTheme() + "/oauth/" + pageName + ".html");
                if (stream != null) {
                    return new InputStreamReader(stream);
                } else {
                    // ... and fall back if none is provided.
                    return new InputStreamReader(config.getServletContext().getResourceAsStream(
                            "/bennu-oauth/" + pageName + ".html"));
                }
            }
        });
        //engine.addExtension(new PortalExtension());
        if (BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()) {
            engine.setTemplateCache(null);
        }

        engine.addExtension(new AbstractExtension() {
            @Override
            public Map<String, Function> getFunctions() {
                Map<String, Function> functions = new HashMap<>();
                functions.put("i18n", new I18NFunction());
                return functions;
            }
        });
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Strings.isNullOrEmpty(request.getPathInfo())) {
            response.sendError(404);
            return;
        }
        String path = trim(request.getPathInfo());
        switch (path) {
        case "userdialog":
            handleUserDialog(request, response);
            break;
        case "userconfirmation":
            if (!"POST".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            userConfirmation(request, response);
            break;
        case "access_token":
            if (!"POST".equals(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            handleAccessToken(request, response);
            break;
        case "refresh_token":
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

    //refreshAccessToken
    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String clientId = request.getParameter(CLIENT_ID);
        String clientSecret = request.getParameter(CLIENT_SECRET);
        String refreshToken = request.getParameter(REFRESH_TOKEN);

        ExternalApplication externalApplication = getExternalApplication(clientId);

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

        String refreshTokenDecoded = "";
        try {
            refreshTokenDecoded = new String(Base64.getDecoder().decode(refreshToken));
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

        if (!externalApplication.matchesSecret(clientSecret)) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (!FenixFramework.isDomainObjectValid(appUserSession) || !appUserSession.matchesRefreshToken(refreshToken)) {
            sendOAuthErrorResponse(response, Status.UNAUTHORIZED, REFRESH_TOKEN_INVALID, REFRESH_TOKEN_DOESN_T_MATCH);
            return;
        }

        String newAccessToken = generateToken(appUserSession);

        appUserSession.setNewAccessToken(newAccessToken);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ACCESS_TOKEN, newAccessToken);
        jsonResponse.addProperty(EXPIRES_IN, OAuthProperties.getConfiguration().getAccessTokenExpirationSeconds());

        sendOAuthResponse(response, Status.OK, jsonResponse);
    }

    //getTokens
    private void handleAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String clientId = request.getParameter(CLIENT_ID);
        String clientSecret = request.getParameter(CLIENT_SECRET);
        String redirectUrl = request.getParameter(REDIRECT_URI);
        String authCode = request.getParameter(CODE);
        String grantType = request.getParameter(GRANT_TYPE);

        if (Strings.isNullOrEmpty(clientId) || Strings.isNullOrEmpty(clientSecret) || Strings.isNullOrEmpty(grantType)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT,
                    Joiner.on(",").join(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE) + " are mandatory");
            return;
        }

        if (!GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType) && !GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            sendOAuthErrorResponse(
                    response,
                    Status.BAD_REQUEST,
                    INVALID_GRANT,
                    GRANT_TYPE + " must be on of the following values: "
                            + Joiner.on(",").join(GRANT_TYPE_CLIENT_CREDENTIALS, GRANT_TYPE_AUTHORIZATION_CODE));
            return;
        }

        if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
            if (Strings.isNullOrEmpty(redirectUrl) || Strings.isNullOrEmpty(authCode)) {
                sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, Joiner.on(",").join(REDIRECT_URI, CODE)
                        + " are mandatory");
                return;
            }
        }

        ExternalApplication externalApplication = getExternalApplication(clientId);

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

        if (!externalApplication.matches(redirectUrl, clientSecret)) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CREDENTIALS_OR_REDIRECT_URI_DON_T_MATCH);
            return;
        }

        if (externalApplication instanceof ServiceApplication) {
            final String accessToken = generateToken(externalApplication);
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

        if (appUserSession.isCodeValid()) {
            String accessToken = generateToken(appUserSession);
            String refreshToken = generateToken(appUserSession);
            appUserSession.setTokens(accessToken, refreshToken);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty(ACCESS_TOKEN, accessToken);
            jsonResponse.addProperty(REFRESH_TOKEN, refreshToken);
            jsonResponse.addProperty(EXPIRES_IN, OAuthProperties.getConfiguration().getAccessTokenExpirationSeconds());
            sendOAuthResponse(response, Status.OK, jsonResponse);
        } else {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CODE_EXPIRED);
        }

    }

    private void handleUserDialog(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clientId = request.getParameter(CLIENT_ID);
        String redirectUrl = request.getParameter(REDIRECT_URI);
        User user = Authenticate.getUser();

        if (!Strings.isNullOrEmpty(clientId) && !Strings.isNullOrEmpty(redirectUrl)) {
            if (user == null) {
                final String cookieValue = clientId + "|" + redirectUrl;
                response.addCookie(new Cookie(OAUTH_SESSION_KEY, Base64.getEncoder().encodeToString(cookieValue.getBytes())));
                response.sendRedirect(request.getContextPath() + "/login?callback="
                        + CoreConfiguration.getConfiguration().applicationUrl() + "/oauth/userdialog");
                return;
            } else {
                redirectToRedirectUrl(request, response, user, clientId, redirectUrl);
                return;
            }
        } else {
            if (user != null) {
                final Cookie cookie = CookieReaderUtils.getCookieForName(OAUTH_SESSION_KEY, request);
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

    private static class I18NFunction implements Function {
        final List<String> variableArgs = Stream.of("arg0", "arg1", "arg2", "arg3", "arg4", "arg5").collect(Collectors.toList());

        @Override
        public List<String> getArgumentNames() {
            return Stream.of("bundle", "key", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5").collect(Collectors.toList());
        }

        @Override
        public Object execute(Map<String, Object> args) {
            String bundle = (String) args.get("bundle");
            String key = args.get("key").toString();
            return BundleUtil.getString(bundle, key, arguments(args));
        }

        public String[] arguments(Map<String, Object> args) {
            List<String> values = new ArrayList<>();
            for (String variableArg : variableArgs) {
                if (args.containsKey(variableArg) && args.get(variableArg) instanceof String) {
                    values.add((String) args.get(variableArg));
                }
            }
            return values.toArray(new String[] {});
        }
    }

    private void authorizationPage(HttpServletRequest request, HttpServletResponse response, ExternalApplication clientApplication)
            throws IOException {
        Map<String, Object> ctx = new HashMap<>();
        PortalConfiguration config = PortalConfiguration.getInstance();
        // Add relevant variables
        ctx.put("config", config);
        ctx.put("app", clientApplication);
        ctx.put("currentLocale", I18N.getLocale());
        ctx.put("contextPath", request.getContextPath());
        ctx.put("locales", CoreConfiguration.supportedLocales());
        ctx.put("loggedUser", Authenticate.getUser());

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
        final int indexOf = cookieValue.indexOf("|");
        String clientApplicationId = cookieValue.substring(0, indexOf);
        String redirectUrl = cookieValue.substring(indexOf + 1, cookieValue.length());
        redirectToRedirectUrl(request, response, user, clientApplicationId, redirectUrl);
    }

    private void redirectToRedirectUrl(HttpServletRequest request, HttpServletResponse response, User user, String clientId,
            String redirectUrl) throws IOException {

        ExternalApplication externalApplication = getExternalApplication(clientId);

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
            authorizationPage(request, response, externalApplication);
            return;
        } else {
            redirectWithCode(request, response, user, externalApplication);
            return;
        }

    }

    private void redirectWithCode(HttpServletRequest request, HttpServletResponse response, User user,
            ExternalApplication clientApplication) throws IOException {
        final String code = createAppUserSession(clientApplication, user, request, response);
        response.sendRedirect(clientApplication.getRedirectUrl() + "?" + CODE + "=" + code);
    }

    public void userConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = Authenticate.getUser();
        if (user == null) {
            errorPage(request, response);
            return;
        }

        String clientId = request.getParameter(CLIENT_ID);
        String redirectUrl = request.getParameter(REDIRECT_URI);

        ExternalApplication externalApplication = FenixFramework.getDomainObject(clientId);

        if (externalApplication == null || externalApplication instanceof ServiceApplication) {
            sendOAuthErrorResponse(response, Status.BAD_REQUEST, INVALID_GRANT, CLIENT_ID_NOT_FOUND);
            return;
        }

        if (!isValidApplication(response, externalApplication)) { // this method sends error response if needed
            return;
        }

        if (externalApplication.matchesUrl(redirectUrl)) {
            redirectWithCode(request, response, user, externalApplication);
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
    private static String createAppUserSession(ExternalApplication application, User user, HttpServletRequest request,
            HttpServletResponse response) {
        String code = generateCode();
        ApplicationUserAuthorization appUserAuthorization =
                application.getApplicationUserAuthorization(user).orElseGet(
                        () -> new ApplicationUserAuthorization(user, application));
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
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.print(jsonResponse.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    private ExternalApplication getExternalApplication(String clientId) {
        DomainObject domainObject = FenixFramework.getDomainObject(clientId);
        if (domainObject == null || !FenixFramework.isDomainObjectValid(domainObject)
                || !(domainObject instanceof ExternalApplication)) {
            return null;
        }

        return (ExternalApplication) domainObject;
    }

    private static String generateCode() {
        return Hashing.sha512().hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString();
    }

    private static String getDeviceId(HttpServletRequest request) {
        String deviceId = request.getParameter(DEVICE_ID);
        if (Strings.isNullOrEmpty(deviceId)) {
            return request.getHeader(HttpHeaders.USER_AGENT);
        }
        return deviceId;
    }

    private String generateToken(DomainObject domainObject) {
        String random = generateCode();
        String token = Joiner.on(":").join(domainObject.getExternalId(), random);
        return Base64.getEncoder().encodeToString(token.getBytes()).replace("=", "").replace("+", "-").replace("/", "-");
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
