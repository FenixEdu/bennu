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

import static pt.ist.fenixframework.FenixFramework.atomic;

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
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

import com.google.gson.JsonElement;

@Path("/bennu-oauth/sessions/")
public class ExternalApplicationAuthorizationSessionResources extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{session}")
    public JsonElement authorizations(@PathParam("session") ApplicationUserAuthorization authorization) {
        User user = verifyAndGetRequestAuthor();

        if (!isManager(user) && authorization.getApplication() instanceof ServiceApplication) {
            return null;
        }

        if (authorization.getUser() == user || isManager(user)) {
            return view(authorization.getSessionSet());
        }

        return null;
    }

    @DELETE
    @Path("/{session}")
    public Response delete(@PathParam("session") ApplicationUserSession session) {
        User user = verifyAndGetRequestAuthor();

        if (!isManager(user) && session.getApplicationUserAuthorization().getApplication() instanceof ServiceApplication) {
            return null;
        }

        if (session.getApplicationUserAuthorization().getUser() == user || isManager(user)) {
            atomic(() -> {
                session.delete();
            });
            return ok();
        }
        return null;
    }

    private boolean isManager(User user) {
        return Group.managers().isMember(user);
    }
}
