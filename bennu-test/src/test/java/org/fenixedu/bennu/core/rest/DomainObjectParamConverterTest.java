package org.fenixedu.bennu.core.rest;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

/***
 * Tests for DomainObjectParamConverter
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 * @see DomainObjectParamConverter
 * @see JerseyTest
 *
 */
@RunWith(FenixFrameworkRunner.class)
public class DomainObjectParamConverterTest extends JerseyTest {

    @Path("resource")
    public static class TestResource {
        @Path("/username")
        @GET
        public String username(@QueryParam("id") User user) {
            return user.getUsername();
        }

        @Path("/username/{id}/email")
        @POST
        public String postUserAsMessageBody(@PathParam("id") User user, @QueryParam("email") String email) {
            setUserEmail(user, email);
            return user.getEmail();
        }

        private void setUserEmail(User user, String email) {
            user.getProfile().setEmail(email);
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class, DomainObjectParamConverter.class);
    }

    private static User user1;

    @BeforeClass
    public static void setupUsers() {
        final String username = "user1";
        FenixFramework.atomic(() -> {
            user1 = User.findByUsername(username);
            if (user1 == null) {
                user1 =
                        new User(username,
                                new UserProfile(username, username, username, username + "@gmail.com", Locale.getDefault()));
            }
        });
    }

    @Test
    public void testDomainObjectAsQueryParam() {
        final String username =
                target("resource").path("username").queryParam("id", user1.getExternalId()).request().get(String.class);

        assertEquals("user1", username);
    }

    @Test
    public void testDomainObjecAsPathParam() {
        final String expectedEmail = "user1@fanfans.com";

        final String newEmail =
                target("resource").path("username").path(user1.getExternalId()).path("email").queryParam("email", expectedEmail)
                        .request().post(null, String.class);

        assertEquals(expectedEmail, newEmail);
        assertEquals(expectedEmail, user1.getEmail());
    }

    @Test(expected = NotFoundException.class)
    public void testNotFoundDomainObjectAsQueryParam() {
        target("resource").path("username").queryParam("id", "THIS_IS_NOT_A_DOMAIN_OBJECT").request().get(String.class);
    }
}
