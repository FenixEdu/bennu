package org.fenixedu.bennu.oauth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;

@Path("/bennu-oauth/test")
public class TestResource {

    @Produces(MediaType.TEXT_PLAIN)
    @Path("/test-scope")
    @GET
    @OAuthEndpoint(value = "TEST")
    public String test() {
        return "this is an endpoint with TEST scope";
    }

    @Produces(MediaType.TEXT_PLAIN)
    @Path("/service-only-with-scope")
    @GET
    @OAuthEndpoint(value = "SERVICE", serviceOnly = true)
    public String test2() {
        return "this is an endpoint with SERVICE scope, serviceOnly";
    }

    @Produces(MediaType.TEXT_PLAIN)
    @Path("/service-only-without-scope")
    @GET
    @OAuthEndpoint(serviceOnly = true)
    public String test3() {
        return "this is an endpoint with serviceOnly";
    }

    @Produces(MediaType.TEXT_PLAIN)
    @GET
    @Path("/normal")
    public String test4() {
        return "this is a normal endpoint";
    }

}
