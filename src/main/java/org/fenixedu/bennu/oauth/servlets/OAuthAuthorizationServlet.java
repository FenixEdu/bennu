package org.fenixedu.bennu.oauth.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CookieReaderUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Servlet implementation class OAuthAuthorizationServlet
 */
@WebServlet("/oauth/*")
public class OAuthAuthorizationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final static String OAUTH_SESSION_KEY = "OAUTH_CLIENT_ID";

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
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = trim(request.getPathInfo());
        switch (path) {
        case "userdialog":
            handleUserDialog(request, response);
            break;
        case "userconfirmation":
            userConfirmation(request, response);
            break;
        case "access_token":
            handleAccessToken(request, response);
            break;
        case "refresh_token":
            handleRefreshToken(request, response);
            break;

        default:
            response.sendError(404);
        }
    }

    //refreshAccessToken
    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        String refreshToken = request.getParameter("refresh_token");

        ExternalApplication externalApplication = FenixFramework.getDomainObject(clientId);

        if (!isValidApplication(response, externalApplication)) {
            System.out.println("not valid");
            //return null;
        }

        String accessTokenDecoded = new String(Base64.getDecoder().decode(refreshToken));
        String[] accessTokenBuilder = accessTokenDecoded.split(":");

        if (accessTokenBuilder.length != 2) {
            System.out.println("token problem");
            //throw new FenixOAuthTokenException();
        }

        String appUserSessionExternalId = accessTokenBuilder[0];

        ApplicationUserSession appUserSession = FenixFramework.getDomainObject(appUserSessionExternalId);

        if (!externalApplication.matchesSecret(clientSecret)) {
            //return sendOAuthResponse(response,
            //        getOAuthProblemResponse(SC_UNAUTHORIZED, INVALID_GRANT, "Credentials or redirect_uri don't match"));
        }

        if (!appUserSession.matchesRefreshToken(refreshToken)) {
            //return sendOAuthResponse(response,
            ///        getOAuthProblemResponse(SC_UNAUTHORIZED, "refreshTokenInvalid", "Refresh token doesn't match"));

        }

        String newAccessToken = generateToken(appUserSession);

        appUserSession.setNewAccessToken(newAccessToken);

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(response.SC_OK);

        PrintWriter printout = response.getWriter();

        JsonObject jObj = new JsonObject();
        jObj.addProperty("access_token", newAccessToken);
        jObj.addProperty("expires_in", "3600");
        printout.print(jObj.toString());
        printout.flush();
    }

    //getTokens
    private void handleAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        String redirectUrl = request.getParameter("redirect_uri");
        String authCode = request.getParameter("code");
        String grantType = request.getParameter("grant_type");

        ExternalApplication externalApplication = FenixFramework.getDomainObject(clientId);

        if (externalApplication == null) {
            //return sendOAuthResponse(response, getOAuthProblemResponse(SC_BAD_REQUEST, INVALID_GRANT, "Client ID not recognized"));
            System.out.println("externalapplication null");
            errorPage(request, response);
        }

        if (!isValidApplication(response, externalApplication)) {
            //return null;
            System.out.println("not valid");
            errorPage(request, response);

        }

        if (!externalApplication.matches(redirectUrl, clientSecret)) {
            //return sendOAuthResponse(response,
            //        getOAuthProblemResponse(SC_BAD_REQUEST, INVALID_GRANT, "Credentials or redirect_uri don't match"));
            errorPage(request, response);

        }

        ApplicationUserSession appUserSession = externalApplication.getApplicationUserSession(authCode);

        if (appUserSession == null) {
            //return sendOAuthResponse(response, getOAuthProblemResponse(SC_BAD_REQUEST, INVALID_GRANT, "Code Invalid"));
            System.out.println("app user session null");
            errorPage(request, response);
        }

        if (appUserSession.isCodeValid()) {
            String accessToken = generateToken(appUserSession);
            String refreshToken = generateToken(appUserSession);
            appUserSession.setTokens(accessToken, refreshToken);

            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(response.SC_OK);

            PrintWriter printout = response.getWriter();

            JsonObject jObj = new JsonObject();
            jObj.addProperty("access_token", accessToken);
            jObj.addProperty("refresh_token", refreshToken);
            jObj.addProperty("expires_in", "3600");
            printout.print(jObj.toString());
            printout.flush();

        } else {
            System.out.println("last");
            //r = getOAuthProblemResponse(SC_BAD_REQUEST, OAuthError.TokenResponse.INVALID_GRANT, "Code expired");
            errorPage(request, response);
        }

    }

    private void handleUserDialog(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clientId = request.getParameter("client_id");
        String redirectUrl = request.getParameter("redirect_uri");
        User user = Authenticate.getUser();

        if (!Strings.isNullOrEmpty(clientId) && !Strings.isNullOrEmpty(redirectUrl)) {
            if (user == null) {
                final String cookieValue = clientId + "|" + redirectUrl;
                response.addCookie(new Cookie(OAUTH_SESSION_KEY, Base64.getEncoder().encodeToString(cookieValue.getBytes())));
                response.sendRedirect(request.getContextPath() + "/login?callback="
                        + CoreConfiguration.getConfiguration().applicationUrl() + "/oauth/userdialog");
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

    public void redirectToRedirectUrl(HttpServletRequest request, HttpServletResponse response, User user, final Cookie cookie)
            throws IOException {
        String cookieValue = new String(Base64.getDecoder().decode(cookie.getValue()));
        final int indexOf = cookieValue.indexOf("|");
        String clientApplicationId = cookieValue.substring(0, indexOf);
        String redirectUrl = cookieValue.substring(indexOf + 1, cookieValue.length());

        redirectToRedirectUrl(request, response, user, clientApplicationId, redirectUrl);
    }

    public void redirectToRedirectUrl(HttpServletRequest request, HttpServletResponse response, User user,
            String clientApplicationId, String redirectUrl) throws IOException {

        final ExternalApplication clientApplication = FenixFramework.getDomainObject(clientApplicationId);

        if (!FenixFramework.isDomainObjectValid(clientApplication) || !clientApplication.matchesUrl(redirectUrl)
                || clientApplication.isBanned() || clientApplication.isDeleted()) {
            errorPage(request, response);
            return;
        }

        //TODO if oficial application
        if (!clientApplication.hasApplicationUserAuthorization(user)) {
            request.setAttribute("application", clientApplication);
            authorizationPage(request, response, clientApplication);
        } else {
            redirectWithCode(request, response, user, clientApplication);
        }
    }

    private void redirectWithCode(HttpServletRequest request, HttpServletResponse response, User user,
            ExternalApplication clientApplication) throws IOException {
        final String code = createAppUserSession(clientApplication, user, request, response);
        response.sendRedirect(clientApplication.getRedirectUrl() + "?code=" + code);
    }

    public void userConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = Authenticate.getUser();
        if (user == null) {
            errorPage(request, response);
        }

        String clientId = request.getParameter("client_id");
        String redirectUrl = request.getParameter("redirect_uri");

        ExternalApplication externalApplication = FenixFramework.getDomainObject(clientId);

        if (!isValidApplication(response, externalApplication)) {
            errorPage(request, response);
        }

        if (externalApplication.matchesUrl(redirectUrl)) {
            redirectWithCode(request, response, user, externalApplication);
        }
        errorPage(request, response);
    }

    private boolean isValidApplication(HttpServletResponse response, ExternalApplication clientApplication) {
        if (clientApplication == null) {
            return false;
        }

        if (clientApplication.isDeleted()) {
            //sendOAuthResponse(response, getOAuthProblemResponse(SC_UNAUTHORIZED, INVALID_GRANT, "The application has been deleted."));
            return false;
        }

        if (clientApplication.isBanned()) {
            //sendOAuthResponse(response, getOAuthProblemResponse(SC_UNAUTHORIZED, INVALID_GRANT, "The application has been banned."));
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

    private static String generateCode() {
        return Hashing.sha512().hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString();
    }

    private static String getDeviceId(HttpServletRequest request) {
        String deviceId = request.getParameter("device_id");
        if (Strings.isNullOrEmpty(deviceId)) {
            return request.getHeader(HttpHeaders.USER_AGENT);
        }
        return deviceId;
    }

    private String generateToken(ApplicationUserSession appUserSession) {
        String random = generateCode();
        String token = Joiner.on(":").join(appUserSession.getExternalId(), random);
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
