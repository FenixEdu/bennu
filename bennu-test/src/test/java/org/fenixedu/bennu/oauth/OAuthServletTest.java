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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.rest.DomainObjectParamConverter;
import org.fenixedu.bennu.core.rest.JsonAwareResource;
import org.fenixedu.bennu.core.rest.JsonBodyReaderWriter;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.oauth.api.ExternalApplicationAuthorizationResources;
import org.fenixedu.bennu.oauth.api.OAuthAuthorizationProvider;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationAdapter;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationAuthorizationAdapter;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationAuthorizationSessionAdapter;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationScopeAdapter;
import org.fenixedu.bennu.oauth.api.json.ServiceApplicationAdapter;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.fenixedu.bennu.oauth.jaxrs.BennuOAuthFeature;
import org.fenixedu.bennu.oauth.servlets.OAuthAuthorizationServlet;
import org.fenixedu.commons.i18n.LocalizedString;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

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
    private static volatile ExternalApplicationScope serviceApplicationOAuthAccessProvider;
    private static volatile ExternalApplicationScope loggedScope;

    private static final Locale enGB = Locale.forLanguageTag("en-GB");

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JsonBodyReaderWriter.class);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class, BennuOAuthFeature.class, ExternalApplicationAuthorizationResources.class,
                JsonBodyReaderWriter.class, DomainObjectParamConverter.class, OAuthAuthorizationProvider.class);
    }

    private static User createUser(String username, String firstName, String lastName, String fullName, String email) {
        return new User(username, new UserProfile(firstName, lastName, fullName, email, Locale.getDefault()));
    }

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

        if (serviceApplicationOAuthAccessProvider == null) {
            serviceApplicationOAuthAccessProvider = new ExternalApplicationScope();
            serviceApplicationOAuthAccessProvider.setScopeKey("OAUTH_AUTHORIZATION_PROVIDER");
            serviceApplicationOAuthAccessProvider
                    .setName(new LocalizedString.Builder().with(enGB, "OAuth Authorization Provider Scope").build());
            serviceApplicationOAuthAccessProvider
                    .setDescription(new LocalizedString.Builder().with(enGB, "OAuth Authorization Provider Scope").build());
        }

        if (loggedScope == null) {
            loggedScope = new ExternalApplicationScope();
            loggedScope.setScopeKey("LOGGED");
            loggedScope.setName(new LocalizedString.Builder().with(enGB, "Logged Scope").build());
            loggedScope.setDescription(new LocalizedString.Builder().with(enGB, "Logged Scope").build());
        }

        JsonAwareResource.setDefault(ExternalApplication.class, ExternalApplicationAdapter.class);
        JsonAwareResource.setDefault(ApplicationUserAuthorization.class, ExternalApplicationAuthorizationAdapter.class);
        JsonAwareResource.setDefault(ApplicationUserSession.class, ExternalApplicationAuthorizationSessionAdapter.class);
        JsonAwareResource.setDefault(ExternalApplicationScope.class, ExternalApplicationScopeAdapter.class);
        JsonAwareResource.setDefault(ServiceApplication.class, ServiceApplicationAdapter.class);

    }

    @BeforeClass
    public static void setup() {
        FenixFramework.atomic(OAuthServletTest::initObjects);
    }

    private String generateToken(DomainObject domainObject) {
        String random = "fenix";
        String token = Joiner.on(":").join(domainObject.getExternalId(), random);
        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)).replace("=", "").replace("+", "-")
                .replace("/", "-");
    }

    @Test
    public void testTokenTypeWrongAccessTokenInHeader() {

        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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
        mockPostRequestWithAuthorizationHeader(req, clientSecret, externalApp.getRedirectUrl(), applicationUserSession.getCode());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have" + ACCESS_TOKEN + " field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field",
                    token.has(TOKEN_TYPE) && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString() + "fenixedu";
            String tokenType = token.get(TOKEN_TYPE).getAsString();

            Response result = target("bennu-oauth").path("test").path("test-scope").request()
                    .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(Response.class);

            Assert.assertEquals("request must fail", 401, result.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWrongTokenTypeInHeader() {

        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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
        mockPostRequestWithAuthorizationHeader(req, clientSecret, externalApp.getRedirectUrl(), applicationUserSession.getCode());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have" + ACCESS_TOKEN + " field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field",
                    token.has(TOKEN_TYPE) && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString();
            String tokenType = token.get(TOKEN_TYPE).getAsString() + "fenixedu";

            Response result = target("bennu-oauth").path("test").path("test-scope").request()
                    .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(Response.class);

            Assert.assertEquals("request must fail", 401, result.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTokenTypeRefreshAccessTokenInHeader() {

        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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

        mockPostRequestWithTokenRefresh(req, externalApp.getExternalId() + ":" + externalApp.getSecret(),
                applicationUserSession.getRefreshToken());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());
            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field",
                    token.has(TOKEN_TYPE) && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString();
            String tokenType = token.get(TOKEN_TYPE).getAsString();

            String result = target("bennu-oauth").path("test").path("test-scope").request()
                    .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(String.class);

            Assert.assertEquals("this is an endpoint with TEST scope user1", result);
        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTokenTypeInHeader() {

        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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
        mockPostRequestWithAuthorizationHeader(req, clientSecret, externalApp.getRedirectUrl(), applicationUserSession.getCode());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();
            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have" + ACCESS_TOKEN + " field",
                    token.has(ACCESS_TOKEN) && token.get(ACCESS_TOKEN).getAsString().length() > 0);

            Assert.assertTrue("response must be a valid json and have " + TOKEN_TYPE + " field",
                    token.has(TOKEN_TYPE) && token.get(TOKEN_TYPE).getAsString().length() > 0);

            String accessToken = token.get(ACCESS_TOKEN).getAsString();
            String tokenType = token.get(TOKEN_TYPE).getAsString();

            String result = target("bennu-oauth").path("test").path("test-scope").request()
                    .header(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken).get(String.class);

            Assert.assertEquals("this is an endpoint with TEST scope user1", result);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAccessTokenHeaderTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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
        mockPostRequestWithAuthorizationHeader(req, clientSecret, externalApp.getRedirectUrl(), applicationUserSession.getCode());

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
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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
        mockPostRequestWithAuthorizationHeader(req, clientSecret, externalApp.getRedirectUrl(), applicationUserSession.getCode());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());
        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void refreshAccessTokenHeaderTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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

        mockPostRequestWithTokenRefresh(req, externalApp.getExternalId() + ":" + externalApp.getSecret(),
                applicationUserSession.getRefreshToken());

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
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
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

        mockPostRequestWithTokenRefresh(req, "fenixedu:fenixedu", applicationUserSession.getRefreshToken());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());
        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenHeaderEmptyTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
        Authenticate.unmock();
        String clientSecret = "";

        mockPostRequestWithClientCredential(req, clientSecret);

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return BAD_REQUEST", 400, res.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenHeaderTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
        Authenticate.unmock();
        String clientSecret = serviceApplication.getExternalId() + ":" + serviceApplication.getSecret();
        mockPostRequestWithClientCredential(req, clientSecret);

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
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();

        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplication.getExternalId(),
                serviceApplication.getSecret());

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
    public void testOAuthEndpointWithoutToken() {
        Authenticate.unmock();

        try {
            target("bennu-oauth").path("test-scope").request().get();
        } catch (WebApplicationException e) {
            Assert.assertNotEquals("invocation of oauth endpoint without tokens must fail", Status.OK.getStatusCode(),
                    e.getResponse().getStatus());
        }
    }

    @Test
    public void testOAuthEndpointWithTokenWithDifferentScope() {
        Authenticate.unmock();

        User user = createUser("testOAuthEndpointWithToken", "John", "Doe", "John Doe", "john.doe@fenixedu.org");

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ExternalApplicationScope testScope = new ExternalApplicationScope();
        testScope.setScopeKey("TEST2");
        testScope.setName(new LocalizedString.Builder().with(enGB, "Test Scope").build());
        testScope.setDescription(new LocalizedString.Builder().with(enGB, "Test Scope").build());

        externalApp.addScopes(testScope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();

        String accessToken = generateToken(applicationUserSession);

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user, externalApp);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        applicationUserSession.setTokens(accessToken, null);

        applicationUserAuthorization.addSession(applicationUserSession);

        try {
            target("bennu-oauth").path("test-scope").queryParam("access_token", accessToken).request().get(String.class);
        } catch (WebApplicationException e) {
            Assert.assertNotEquals("invocation of oauth endpoint with wrong scope must fail", Status.OK.getStatusCode(),
                    e.getResponse().getStatus());
        } finally {
            testScope.setBennu(null);
        }
    }

    @Test
    public void testServiceOnlyEndpoint() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();

        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplication.getExternalId(),
                serviceApplication.getSecret());

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();
            final String accessToken = token.get(ACCESS_TOKEN).getAsString();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && accessToken.length() > 0);

            String result = target("bennu-oauth").path("test").path("service-only-without-scope")
                    .queryParam(ACCESS_TOKEN, accessToken).request().get(String.class);
            Assert.assertEquals("this is an endpoint with serviceOnly", result);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testServiceOnlyEndpointWithScopeMustFail() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();

        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplication.getExternalId(),
                serviceApplication.getSecret());

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();
            final String accessToken = token.get(ACCESS_TOKEN).getAsString();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && accessToken.length() > 0);

            Response result = target("bennu-oauth").path("test").path("service-only-with-scope")
                    .queryParam(ACCESS_TOKEN, accessToken).request().get(Response.class);

            Assert.assertNotEquals("request must fail", 200, result.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testServiceOnlyWithScopeEndpoint() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();

        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplicationWithScope.getExternalId(),
                serviceApplicationWithScope.getSecret());

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final JsonObject token = new JsonParser().parse(tokenJson).getAsJsonObject();
            final String accessToken = token.get(ACCESS_TOKEN).getAsString();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has(ACCESS_TOKEN) && accessToken.length() > 0);

            String result = target("bennu-oauth").path("test").path("service-only-with-scope")
                    .queryParam(ACCESS_TOKEN, accessToken).request().get(String.class);
            Assert.assertEquals("this is an endpoint with SERVICE scope, serviceOnly", result);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenWithWrongGrantTypeTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();

        mockPostRequestWithGrantType(req, GRANT_TYPE_AUTHORIZATION_CODE, serviceApplication.getExternalId(),
                serviceApplication.getSecret());

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getServiceAccessTokenWithWrongClientSecretTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();

        String clientSecret = serviceApplication.getExternalId() + ":lasdlkasldksladkalskdsal";
        String encodedClientSecret = Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8));
        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplication.getExternalId(), encodedClientSecret);

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status BAD_REQUEST", 400, res.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testOAuthServletAccessTokenRequestWithLoginExpired() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
        Authenticate.unmock();

        User user = createUser("testOAuthServletAccessTokenRequestWithLoginExpired", "John", "Doe", "John Doe",
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

        mockPostRequestWithAuthorizationHeader(req, externalApp.getExternalId() + ":" + externalApp.getSecret(),
                externalApp.getRedirectUrl(), applicationUserSession.getCode());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return bad request", Status.BAD_REQUEST.getStatusCode(), res.getStatus());

            user.openLoginPeriod();
            res = new TestHttpServletResponse();
            oauthServlet.service(req, res);

            final JsonObject token = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has("access_token") && token.get("access_token").getAsString().length() > 0);

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testOAuthServletRefreshTokenRequestWithLoginExpired() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
        Authenticate.unmock();

        User user = createUser("testOAuthServletRefreshTokenRequestWithLoginExpired", "John", "Doe", "John Doe",
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

        mockPostRequestWithTokenRefresh(req, externalApp.getExternalId() + ":" + externalApp.getSecret(),
                applicationUserSession.getRefreshToken());

        try {
            oauthServlet.service(req, res);
            Assert.assertEquals("must return bad request", Status.BAD_REQUEST.getStatusCode(), res.getStatus());

            user.openLoginPeriod();
            res = new TestHttpServletResponse();
            oauthServlet.service(req, res);

            final JsonObject token = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();

            Assert.assertTrue("response must be a valid json and have access_token field",
                    token.has("access_token") && token.get("access_token").getAsString().length() > 0);

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
        externalApp.addScopes(loggedScope);

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();

        String accessToken = generateToken(applicationUserSession);

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user, externalApp);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        applicationUserSession.setTokens(accessToken, null);

        applicationUserAuthorization.addSession(applicationUserSession);

        user.closeLoginPeriod();

        Response responseKO = target("bennu-oauth").path("test").path("test-scope-with-logged-user")
                .queryParam("access_token", accessToken).request().get();

        Assert.assertEquals(Status.UNAUTHORIZED, responseKO.getStatusInfo());

        user.openLoginPeriod();

        String result = target("bennu-oauth").path("test").path("test-scope-with-logged-user")
                .queryParam("access_token", accessToken).request().get(String.class);

        Assert.assertEquals("this is an endpoint with TEST scope: testEndpointWithAccessTokenLoginExpired", result);
    }

    @Test
    public void testServiceApplicationOAuthAccessProvider() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
        Authenticate.unmock();

        User user = createUser("testServiceApplicationOAuthAccessProvider", "John", "Doe", "John Doe", "john.doe@fenixedu.org");

        ServiceApplication serviceApplication = new ServiceApplication();
        serviceApplication.setAuthor(user1);
        serviceApplication.addScopes(serviceApplicationOAuthAccessProvider);
        serviceApplication.addScopes(loggedScope);

        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplication.getExternalId(),
                serviceApplication.getSecret());

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final String serviceAccessToken =
                    new JsonParser().parse(tokenJson).getAsJsonObject().get("access_token").getAsString();

            String result = target("oauth").path("provider").path(serviceApplication.getExternalId()).path(user.getUsername())
                    .queryParam("access_token", serviceAccessToken).request().post(null, String.class);

            Authenticate.unmock();

            final String userAccessToken = new JsonParser().parse(result).getAsJsonObject().get("access_token").getAsString();

            result = target("bennu-oauth").path("test").path("test-scope-with-logged-user")
                    .queryParam("access_token", userAccessToken).request().get(String.class);

            Assert.assertEquals("this is an endpoint with TEST scope: testServiceApplicationOAuthAccessProvider", result);

            Authenticate.mock(user, "OAuth Access Token");

            JsonArray authorizations =
                    target("bennu-oauth").path("authorizations").request().get(JsonElement.class).getAsJsonArray();

            Assert.assertEquals("no authorizations because it is a service application", 0, authorizations.size());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            serviceApplication.removeScope(serviceApplicationOAuthAccessProvider);
            serviceApplication.removeScope(loggedScope);
        }

    }

    @Test
    public void testServiceApplicationWithUnexistingScope() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        TestHttpServletResponse res = new TestHttpServletResponse();
        Authenticate.unmock();

        User user = createUser("testServiceApplicationWithUnexistingScope", "John", "Doe", "John Doe", "john.doe@fenixedu.org");

        ServiceApplication serviceApplication = new ServiceApplication();
        serviceApplication.setAuthor(user);

        mockPostRequestWithGrantType(req, GRANT_TYPE_CLIENT_CREDENTIALS, serviceApplication.getExternalId(),
                serviceApplication.getSecret());

        try {
            oauthServlet.service(req, res);

            Assert.assertEquals("must return status OK", 200, res.getStatus());

            String tokenJson = res.getContentAsString();

            final String serviceAccessToken =
                    new JsonParser().parse(tokenJson).getAsJsonObject().get("access_token").getAsString();

            Response response = target("bennu-oauth").path("test").path("service-only-with-unexisting-scope")
                    .queryParam("access_token", serviceAccessToken).request().get();

            Assert.assertNotEquals("request must fail since scope does not exist", 200, response.getStatus());

        } catch (ServletException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    private static void mockPostRequestWithGrantType(HttpServletRequest req, String grantType, String clientId, String clientSecret) {
        when(req.getParameter("client_id")).thenReturn(clientId);
        when(req.getParameter("client_secret")).thenReturn(clientSecret);
        when(req.getParameter(GRANT_TYPE)).thenReturn(grantType);
        when(req.getMethod()).thenReturn("POST");
        when(req.getPathInfo()).thenReturn("/access_token");
    }

    private static void mockPostRequestWithAuthorizationHeader(HttpServletRequest req, String clientSecret, String redirectUrl, String code) {
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        when(req.getParameter(REDIRECT_URI)).thenReturn(redirectUrl);
        when(req.getParameter(CODE)).thenReturn(code);
        when(req.getParameter(GRANT_TYPE)).thenReturn(GRANT_TYPE_AUTHORIZATION_CODE);
        when(req.getMethod()).thenReturn("POST");
        when(req.getPathInfo()).thenReturn("/access_token");
    }

    private static void mockPostRequestWithTokenRefresh(HttpServletRequest req, String clientSecret, String token) {
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        when(req.getParameter(REFRESH_TOKEN)).thenReturn(token);
        when(req.getParameter(GRANT_TYPE)).thenReturn(GRANT_TYPE_REFRESH_TOKEN);
        when(req.getMethod()).thenReturn("POST");
        when(req.getPathInfo()).thenReturn("/refresh_token");
    }

    private static void mockPostRequestWithClientCredential(HttpServletRequest req, String clientSecret) {
        when(req.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Basic " + Base64.getEncoder().encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8)));
        when(req.getParameter(GRANT_TYPE)).thenReturn(GRANT_TYPE_CLIENT_CREDENTIALS);
        when(req.getMethod()).thenReturn("POST");
        when(req.getPathInfo()).thenReturn("/access_token");
    }

    @Test
    public void testApplicationWithUnexistingScope() {
        Authenticate.unmock();

        User user = createUser("testApplicationWithUnexistingScope", "John", "Doe", "John Doe", "john.doe@fenixedu.org");

        ExternalApplication externalApp = new ExternalApplication();
        externalApp.setAuthor(user);
        externalApp.setName("Test External Application");
        externalApp.setDescription("This is a test external application");
        externalApp.setRedirectUrl("http://test.url/callback");

        ApplicationUserSession applicationUserSession = new ApplicationUserSession();

        String accessToken = generateToken(applicationUserSession);

        ApplicationUserAuthorization applicationUserAuthorization = new ApplicationUserAuthorization(user, externalApp);
        externalApp.addApplicationUserAuthorization(applicationUserAuthorization);

        applicationUserSession.setTokens(accessToken, null);

        applicationUserAuthorization.addSession(applicationUserSession);

        try {
            target("bennu-oauth").path("test").path("test-scope-with-unexisting-scope").queryParam("access_token", accessToken)
                    .request().get(String.class);

        } catch (WebApplicationException e) {
            Assert.assertNotEquals("request must fail since scope does not exist", 200, e.getResponse().getStatus());
        }

        try {
            target("bennu-oauth").path("test").path("service-only-with-unexisting-scope").queryParam("access_token", accessToken)
                    .request().get(String.class);

        } catch (WebApplicationException e) {
            Assert.assertNotEquals("request must fail since scope does not exist", 200, e.getResponse().getStatus());
        }

    }

}
