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
package org.fenixedu.bennu.oauth.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

import com.google.gson.JsonElement;

@Path("/bennu-oauth/authorizations")
public class ExternalApplicationAuthorizationResources extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement myAuthorizations() {
        return view(verifyAndGetRequestAuthor().getApplicationUserAuthorizationSet().stream()
                .filter(auth -> !(auth.getApplication() instanceof ServiceApplication)));
    }

    @DELETE
    @Path("/{authorization}")
    public Response delete(@PathParam("authorization") ApplicationUserAuthorization authorization) {
        User user = verifyAndGetRequestAuthor();
        if (authorization.getUser() == user || isManager(user)) {
            authorization.delete();
            return ok();
        }
        return null;
    }

    private boolean isManager(User user) {
        return Group.managers().isMember(user);
    }

}
