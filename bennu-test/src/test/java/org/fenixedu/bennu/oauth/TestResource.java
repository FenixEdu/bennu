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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;

@Path("/bennu-oauth/test")
public class TestResource extends BennuRestResource {

    @Produces(MediaType.TEXT_PLAIN)
    @Path("/test-scope")
    @GET
    @OAuthEndpoint(value = "TEST")
    public String testScope() {
        return "this is an endpoint with TEST scope "
                + (Authenticate.getUser() == null ? "" : Authenticate.getUser().getUsername());
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

    @Produces(MediaType.TEXT_PLAIN)
    @Path("/test-scope-with-logged-user")
    @GET
    @OAuthEndpoint(value = "LOGGED")
    public String testWithLoggedUser() {
        return "this is an endpoint with TEST scope: " + verifyAndGetRequestAuthor().getUsername();
    }

}
