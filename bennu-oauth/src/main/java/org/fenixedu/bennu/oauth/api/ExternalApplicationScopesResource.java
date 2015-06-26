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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

import com.google.gson.JsonElement;

@Path("/bennu-oauth/scopes")
public class ExternalApplicationScopesResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement getScopes() {
        verifyAndGetRequestAuthor();
        return view(Bennu.getInstance().getScopesSet().stream().filter(s -> !s.getService()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public JsonElement getAllScopes() {
        accessControl(Group.managers());
        return view(Bennu.getInstance().getScopesSet());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonElement createScope(JsonElement json) {
        accessControl(Group.managers());
        return view(create(json, ExternalApplicationScope.class));
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{scope}")
    public JsonElement updateScope(@PathParam("scope") ExternalApplicationScope scope, JsonElement json) {
        accessControl(Group.managers());
        return view(update(json, scope));
    }

    @DELETE
    @Path("/{scope}")
    public Response delete(@PathParam("scope") ExternalApplicationScope scope) {
        accessControl(Group.managers());
        atomic(() -> {
            for (ExternalApplication externalApplication : Bennu.getInstance().getApplicationsSet()) {
                externalApplication.removeScope(scope);
            }
            Bennu.getInstance().removeScopes(scope);
        });
        return ok();
    }

}
