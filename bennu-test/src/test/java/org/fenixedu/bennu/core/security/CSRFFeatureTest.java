package org.fenixedu.bennu.core.security;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CSRFFeatureTest extends JerseyTest {

    @Path("resource")
    public static class TestResource {

        @GET
        public String get() {
            return "GET - ok";
        }

        @POST
        public String post() {
            return "POST - ok";
        }

        @POST
        @SkipCSRF
        @Path("/nocheck")
        public String postWithoutCheck() {
            return "POST - NOCHECK - ok";
        }

        @PUT
        public String put() {
            return "PUT - ok";
        }

        @PUT
        @SkipCSRF
        @Path("/nocheck")
        public String putWithoutCheck() {
            return "PUT - NOCHECK - ok";
        }

        @DELETE
        public String delete() {
            return "DELETE - ok";
        }

        @DELETE
        @SkipCSRF
        @Path("/nocheck")
        public String deleteWithoutCheck() {
            return "DELETE - NOCHECK - ok";
        }

    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class, CSRFFeature.class);
    }

    // GET test

    @Test
    public void testGETRequestsAreNotAffected() {
        assertEquals("GET - ok", target("/resource").request().get(String.class));
    }

    // POST tests

    @Test(expected = BadRequestException.class)
    public void testPOSTRequestsAreProperlyFiltered() {
        target("/resource").request().post(Entity.html("ignore_me"), String.class);
    }

    @Test
    public void testPOSTRequestsWithHeaderWork() {
        assertEquals("POST - ok",
                target("/resource").request().header("X-Requested-With", "XPTO").post(Entity.html("ignore_me"), String.class));
    }

    @Test
    public void testPOSTRequestsWithIgnoreAnnotationAreNotAffected() {
        assertEquals("POST - NOCHECK - ok",
                target("/resource").path("/nocheck").request().post(Entity.html("ignore_me"), String.class));
    }

    // POST tests

    @Test(expected = BadRequestException.class)
    public void testPUTRequestsAreProperlyFiltered() {
        target("/resource").request().put(Entity.html("ignore_me"), String.class);
    }

    @Test
    public void testPUTRequestsWithHeaderWork() {
        assertEquals("PUT - ok",
                target("/resource").request().header("X-Requested-With", "XPTO").put(Entity.html("ignore_me"), String.class));
    }

    @Test
    public void testPUTRequestsWithIgnoreAnnotationAreNotAffected() {
        assertEquals("PUT - NOCHECK - ok",
                target("/resource").path("/nocheck").request().put(Entity.html("ignore_me"), String.class));
    }

    // DELETE tests

    @Test(expected = BadRequestException.class)
    public void testDELETERequestsAreProperlyFiltered() {
        target("/resource").request().delete(String.class);
    }

    @Test
    public void testDELETERequestsWithHeaderWork() {
        assertEquals("DELETE - ok", target("/resource").request().header("X-Requested-With", "XPTO").delete(String.class));
    }

    @Test
    public void testDELETERequestsWithIgnoreAnnotationAreNotAffected() {
        assertEquals("DELETE - NOCHECK - ok", target("/resource").path("/nocheck").request().delete(String.class));
    }

}
