package org.fenixedu.bennu.oauth;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;
import org.fenixedu.bennu.oauth.servlets.OAuthAuthorizationServlet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(FenixFrameworkRunner.class)
public class OAuthServletTest {

    private static ExternalApplication externalApplication;
    private static ServiceApplication serviceApplication;
    private static OAuthAuthorizationServlet oauthServlet;
    private static User user1;

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

        if (externalApplication == null) {
            externalApplication = new ExternalApplication();
            externalApplication.setAuthor(user1);
            serviceApplication.setName("Test External Application");
            serviceApplication.setDescription("This is a test external application");
            serviceApplication.setRedirectUrl("http://test.url/callback");
        }
    }

    @BeforeClass
    public static void setup() {
        initObjects();
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

            Assert.assertTrue("response must be a valid json and have access_token field", token.has("access_token")
                    && token.get("access_token").getAsString().length() > 0);

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
        req.addParameter("client_secret",
                BaseEncoding.base64().encode((serviceApplication.getExternalId() + ":lasdlkasldksladkalskdsal").getBytes()));
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
}
