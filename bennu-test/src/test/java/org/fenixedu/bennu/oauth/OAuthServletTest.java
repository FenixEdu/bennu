/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth Test.
 *
 * Bennu OAuth Test is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth Test is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth Test.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.oauth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.fenixedu.bennu.oauth.jaxrs.BennuOAuthFeature;
import org.fenixedu.bennu.oauth.servlets.OAuthAuthorizationServlet;
import org.fenixedu.commons.i18n.LocalizedString;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

import com.google.common.base.Joiner;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class OAuthServletTest extends JerseyTest {

    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private static final String GRANT_TYPE = "grant_type";

    private final static String REDIRECT_URI = "redirect_uri";
    private final static String CODE = "code";
    private final static String REFRESH_TOKEN = "refresh_token";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String TOKEN_TYPE = "token_type";

    private static volatile ExternalApplication externalApplication;
    private static volatile ServiceApplication serviceApplication;
    private static volatile OAuthAuthorizationServlet oauthServlet;
    private static volatile User user1;
    private static volatile ServiceApplication serviceApplicationWithScope;
    private static volatile ExternalApplicationScope externalApplicationScope;

    private static final Locale enGB = Locale.forLanguageTag("en-GB");

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class, BennuOAuthFeature.class);
    }

    private static User createUser(String username, String firstName, String lastName, String fullName, String email) {
        return new User(username, new UserProfile(firstName, lastName, fullName, email, Locale.getDefault()));
    }

    @Atomic(mode = TxMode.WRITE)
    public static void initObjects() {

        if (user1 == null) {
            user1 = createUser("user1", "John", "Doe", "John Doe", "john.doe@fenixedu.org");
        }

        if (oauthServlet == null) {
            oauthServlet = new OAuthAuthorizationServlet();
        }

        if (serviceApplication == null) {
            serviceApplication = new ServiceApplication();
            serviceApplication.setAuthor(user1);
            serviceApplication.setName("Test Service Application");
            serviceApplication.setDescription("This is a test service application");
        }

        if (serviceApplicationWithScope == null) {
            serviceApplicationWithScope = new ServiceApplication();
            serviceApplicationWithScope.setAuthorName("John Doe");
            serviceApplicationWithScope.setName("Service App with scope");
            serviceApplicationWithScope.setDescription("Service App with scope SERVICE");
            ExternalApplicationScope scope = new ExternalApplicationScope();
            scope.setScopeKey("SERVICE");
            scope.setName(new LocalizedString.Builder().with(enGB, "Service Scope").build());
            scope.setDescription(new LocalizedString.Builder().with(enGB, "Service scope is for service only").build());
            scope.setService(Boolean.TRUE);
            serviceApplicationWithScope.addScopes(scope);
        }

        if (externalApplication == null) {
            externalApplication = new ExternalApplication();
            externalApplication.setAuthor(user1);
            externalApplication.setName("Test External Application");
            externalApplication.setDescription("This is a test external application");
            externalApplication.setRedirectUrl("http://test.url/callback");
        }

        if (externalApplicationScope == null) {
            externalApplicationScope = new ExternalApplicationScope();
            externalApplicationScope.setScopeKey("TEST");
            externalApplicationScope.setName(new LocalizedString.Builder().with(enGB, "TEST Scope").build());
            externalApplicationScope.setDescription(new LocalizedString.Builder().with(enGB, "TEST scope").build());
            externalApplicationScope.setService(Boolean.FALSE);
        }

    }

    @BeforeClass
    public static void setup() {
        initObjects();
    }

    private String generateToken(DomainObject domainObject) {
        String random = "fenix";
        String token = Joiner.on(":").join(domainObject.getExternalId(), random);
        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)).replace("=", "").replace("+", "-")
                .replace("/", "-");
    }

    @Test
    public void testTokenTypeWrongAccessTokenInHeader() {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");
        externalApp.addScopes(externalApplicationScope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setCode("fenixedu");

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REDIRECT_URI, externalApp.getRedirectUrl());
        req.addParameter(CODE, applicationUserSession.getCode());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have" + ACCESS_TOKEN + " field", token.has(ACCESS_TOKEN)
                    && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field", token.has(TOKEN_TYPE)
                    && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString() + "fenixedu";
            String tokenType = token.get(TOKEN_TYPE).getAsString();

            Response result =
                    target("bennu-oauth").path("test").path("test-scope").request()
                            .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(Response.class);

            Assert.assertEquals("request must fail", 401, result.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWrongTokenTypeInHeader() {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");
        externalApp.addScopes(externalApplicationScope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setCode("fenixedu");

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REDIRECT_URI, externalApp.getRedirectUrl());
        req.addParameter(CODE, applicationUserSession.getCode());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have" + ACCESS_TOKEN + " field", token.has(ACCESS_TOKEN)
                    && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field", token.has(TOKEN_TYPE)
                    && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString();
            String tokenType = token.get(TOKEN_TYPE).getAsString() + "fenixedu";

            Response result =
                    target("bennu-oauth").path("test").path("test-scope").request()
                            .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(Response.class);

            Assert.assertEquals("request must fail", 401, result.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTokenTypeRefreshAccessTokenInHeader() {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");
        externalApp.addScopes(externalApplicationScope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setTokens(generateToken(applicationUserSession), generateToken(applicationUserSession));

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);

        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REFRESH_TOKEN, applicationUserSession.getRefreshToken());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
        req.setMethod("POST");
        req.setPathInfo("/refresh_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());
            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field", token.has(TOKEN_TYPE)
                    && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString();
            String tokenType = token.get(TOKEN_TYPE).getAsString();

            String result =
                    target("bennu-oauth").path("test").path("test-scope").request()
                            .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(String.class);

            Assert.assertEquals("this is an endpoint with TEST scope user1", result);
        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTokenTypeInHeader() {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");
        externalApp.addScopes(externalApplicationScope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setCode("fenixedu");

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REDIRECT_URI, externalApp.getRedirectUrl());
        req.addParameter(CODE, applicationUserSession.getCode());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have" + ACCESS_TOKEN + " field", token.has(ACCESS_TOKEN)
                    && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field", token.has(TOKEN_TYPE)
                    && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString();
            String tokenType = token.get(TOKEN_TYPE).getAsString();

            String result =
                    target("bennu-oauth").path("test").path("test-scope").request()
                            .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(String.class);

            Assert.assertEquals("this is an endpoint with TEST scope user1", result);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAccessTokenHeaderTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setCode("fenixedu");

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REDIRECT_URI, externalApp.getRedirectUrl());
        req.addParameter(CODE, applicationUserSession.getCode());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAccessTokenWrongClientIdHeaderTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setCode("fenixedu");

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = "fenixedu:fenixedu";
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REDIRECT_URI, externalApp.getRedirectUrl());
        req.addParameter(CODE, applicationUserSession.getCode());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());
        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void refreshAccessTokenHeaderTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setTokens(generateToken(applicationUserSession), generateToken(applicationUserSession));

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);

        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REFRESH_TOKEN, applicationUserSession.getRefreshToken());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
        req.setMethod("POST");
        req.setPathInfo("/refresh_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());
            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void refreshAccessTokenWrongClientHeaderRefreshTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setTokens(generateToken(applicationUserSession), generateToken(applicationUserSession));

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user1, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);

        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = "fenixedu:fenixedu";
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REFRESH_TOKEN, applicationUserSession.getRefreshToken());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
        req.setMethod("POST");
        req.setPathInfo("/refresh_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());
        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenHeaderEmptyTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();
        String clientSecret = "";

        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return BAD_REQUEST", 400, res.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenHeaderTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();
        String clientSecret = serviceApplication.getExternalId() + ":" + serviceApplication.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.addParameter("client_id", serviceApplication.getExternalId());
        req.addParameter("client_secret", serviceApplication.getSecret());
        req.addParameter("grant_type", "client_credentials");
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testNormalEndpoint() {
        String res = target("bennu-oauth").path("test").path("normal").request().get(String.class);
        Assert.assertEquals("message must be the same", "this is a normal endpoint", res);
    }

    @Test
    public void testServiceOnlyEndpoint() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.addParameter("client_id", serviceApplication.getExternalId());
        req.addParameter("client_secret", serviceApplication.getSecret());
        req.addParameter("grant_type", "client_credentials");
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();
            final String accessToken = token.get(ACCESS_TOKEN).getAsString();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && accessToken.length() > 0);

            String result =
                    target("bennu-oauth").path("test").path("service-only-without-scope").queryParam(ACCESS_TOKEN, accessToken)
                            .request().get(String.class);
            Assert.assertEquals("this is an endpoint with serviceOnly", result);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testServiceOnlyEndpointWithScopeMustFail() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.addParameter("client_id", serviceApplication.getExternalId());
        req.addParameter("client_secret", serviceApplication.getSecret());
        req.addParameter("grant_type", "client_credentials");
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();
            final String accessToken = token.get(ACCESS_TOKEN).getAsString();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && accessToken.length() > 0);

            Response result =
                    target("bennu-oauth").path("test").path("service-only-with-scope").queryParam(ACCESS_TOKEN, accessToken)
                            .request().get(Response.class);

            Assert.assertNotEquals("request must fail", 200, result.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testServiceOnlyWithScopeEndpoint() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.addParameter("client_id", serviceApplicationWithScope.getExternalId());
        req.addParameter("client_secret", serviceApplicationWithScope.getSecret());
        req.addParameter("grant_type", "client_credentials");
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();
            final String accessToken = token.get(ACCESS_TOKEN).getAsString();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && accessToken.length() > 0);

            String result =
                    target("bennu-oauth").path("test").path("service-only-with-scope").queryParam(ACCESS_TOKEN, accessToken)
                            .request().get(String.class);
            Assert.assertEquals("this is an endpoint with SERVICE scope, serviceOnly", result);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenWithWrongGrantTypeTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.addParameter("client_id", serviceApplication.getExternalId());
        req.addParameter("client_secret", serviceApplication.getSecret());
        req.addParameter("grant_type", "authorization_code");
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenWithWrongClientSecretTest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.addParameter("client_id", serviceApplication.getExternalId());
        req.addParameter(
                "client_secret",
                BaseEncoding.base64().encode(
                        (serviceApplication.getExternalId() + ":lasdlkasldksladkalskdsal").getBytes(StandardCharsets.UTF_8)));
        req.addParameter("grant_type", "client_credentials");
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testOAuthServletAccessTokenRequestWithLoginExpired() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        User user =
                createUser("testOAuthServletAccessTokenRequestWithLoginExpired", "John", "Doe", "John Doe",
                        "john.doe@fenixedu.org");

        user.closeLoginPeriod();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setCode("fenixedu");

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REDIRECT_URI, externalApp.getRedirectUrl());
        req.addParameter(CODE, applicationUserSession.getCode());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        req.setMethod("POST");
        req.setPathInfo("/access_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return bad request", Status.BAD_REQUEST.getStatusCode(), res.getStatus());

            user.openLoginPeriod();
            res = new MockHttpServletResponse();
            oauthServlet.service(req, res);

            final JsonObject token = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field", token.has("access_token")
                    && token.get("access_token").getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testOAuthServletRefreshTokenRequestWithLoginExpired() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authenticate.unmock();

        User user =
                createUser("testOAuthServletRefreshTokenRequestWithLoginExpired", "John", "Doe", "John Doe",
                        "john.doe@fenixedu.org");

        user.closeLoginPeriod();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();
        applicationUserSession.setTokens(generateToken(applicationUserSession), generateToken(applicationUserSession));

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user, externalApp);
        applicationUserAuthorization.addSession(applicationUserSession);

        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        String clientSecret = externalApp.getExternalId() + ":" + externalApp.getSecret();
        req.addHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        req.addParameter(REFRESH_TOKEN, applicationUserSession.getRefreshToken());
        req.addParameter(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
        req.setMethod("POST");
        req.setPathInfo("/refresh_token");

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return bad request", Status.BAD_REQUEST.getStatusCode(), res.getStatus());

            user.openLoginPeriod();
            res = new MockHttpServletResponse();
            oauthServlet.service(req, res);

            final JsonObject token = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field", token.has("access_token")
                    && token.get("access_token").getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void testEndpointWithAccessTokenLoginExpired() {

        User user = createUser("testEndpointWithAccessTokenLoginExpired", "John", "Doe", "John Doe", "john.doe@fenixedu.org");
        Authenticate.unmock();

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user1);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");
        ExternalApplicationScope scope = new ExternalApplicationScope();
        scope.setScopeKey("LOGGED");
        externalApp.addScopes(scope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();

        String accessToken = generateToken(applicationUserSession);

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user, externalApp);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        applicationUserSession.setTokens(accessToken, null);

        applicationUserAuthorization.addSession(applicationUserSession);

        user.closeLoginPeriod();

        Response responseKO =
                target("bennu-oauth").path("test").path("test-scope-with-logged-user").queryParam("access_token", accessToken)
                        .request().get();

        Assert.assertEquals(Status.UNAUTHORIZED, responseKO.getStatusInfo());

        user.openLoginPeriod();

        String result =
                target("bennu-oauth").path("test").path("test-scope-with-logged-user").queryParam("access_token", accessToken)
                        .request().get(String.class);

        Assert.assertEquals("this is an endpoint with TEST scope: testEndpointWithAccessTokenLoginExpired", result);
    }

}
