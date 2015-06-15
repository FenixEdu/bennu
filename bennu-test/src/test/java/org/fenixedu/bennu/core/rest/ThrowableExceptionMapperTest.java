package org.fenixedu.bennu.core.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ThrowableExceptionMapperTest extends JerseyTest {

    @Path("/test")
    public static class TestResource {

        @GET
        @Path("/ok")
        public Response ok() {
            return Response.ok().build();
        }

        @GET
        @Path("/exception")
        public Response exception() throws IOException {
            throw new IOException();
        }

        @GET
        @Path("/web-app-exception")
        public Response webappException() {
            throw new WebApplicationException(Response.accepted().build());
        }

    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class, ThrowableExceptionMapper.class);
    }

    @Test
    public void testRegularRequestsAreNotAffected() {
        Assert.assertEquals(HttpServletResponse.SC_OK, target("test").path("ok").request().get().getStatus());
    }

    @Test
    public void testRegularExceptions() {
        Assert.assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, target("test").path("exception").request().get()
                .getStatus());
    }

    @Test
    public void testWebApplicationExceptions() {
        Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, target("test").path("web-app-exception").request().get().getStatus());
    }
}
