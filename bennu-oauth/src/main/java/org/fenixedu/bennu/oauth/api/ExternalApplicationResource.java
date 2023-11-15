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

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationForManagersAdapter;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationUsersAdapter;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;

@Path("/bennu-oauth/applications")
public class ExternalApplicationResource extends BennuRestResource {

    protected boolean isManager(User user) {
        return Group.managers().isMember(user);
    }

    private boolean isDeveloper(User user) {
        return Group.parse("#developers").isMember(user);
    }

    protected User verifyAndGetRequestAuthor(String applicationId) {
        User currentUser = super.verifyAndGetRequestAuthor();

        if (isManager(currentUser)) {
            return currentUser;
        }

        if (isDeveloper(currentUser) && getApplicationForClientId(applicationId).getAuthor().equals(currentUser)) {
            return currentUser;
        }

        throw AuthorizationException.unauthorized();
    }

    @Override
    protected User verifyAndGetRequestAuthor() {
        User currentUser = super.verifyAndGetRequestAuthor();

        if (isDeveloper(currentUser)) {
            return currentUser;
        }

        throw AuthorizationException.unauthorized();
    }

    protected ExternalApplication getApplicationForClientId(String clientId) {
        return getAllApplications().stream()
                .filter(a -> a.getClientId().equals(clientId))
                .findAny().orElseThrow(() -> BennuCoreDomainException.resourceNotFound(clientId));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement myApplications() {
        return view(verifyAndGetRequestAuthor().getOwnedApplicationSet().stream().filter(p -> p.isActive()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{app}/authorizations")
    public JsonElement applicationsAuthorizations(@PathParam("app") String applicationId) {
        accessControl(Group.managers());
        return view(getApplicationForClientId(applicationId), ExternalApplicationUsersAdapter.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public JsonElement allApplications() {
        accessControl(Group.managers());
        return view(getAllApplications());
    }

    protected Set<? extends ExternalApplication> getAllApplications() {
        return Bennu.getInstance().getApplicationsSet();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonElement createApplication(JsonElement json) {
        verifyAndGetRequestAuthor();
        return view(create(json));
    }

    protected ExternalApplication create(JsonElement json) {
        return create(json, ExternalApplication.class);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{app}")
    public JsonElement updateApplication(@PathParam("app") String application, JsonElement json) {
        User currentUser = verifyAndGetRequestAuthor(application);
        return update(application, json, currentUser);
    }

    protected JsonElement update(String applicationId, JsonElement json, User currentUser) {
        if (isManager(currentUser)) {
            return view(update(json, getApplicationForClientId(applicationId), ExternalApplicationForManagersAdapter.class));
        }
        return view(update(json, getApplicationForClientId(applicationId)));
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{app}/ban")
    public Response banApplication(@PathParam("app") String applicationId, JsonElement json) {
        accessControl(Group.managers());
        atomic(() -> {
            getApplicationForClientId(applicationId).setBanned();
        });
        return ok();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{app}/active")
    public Response unbanApplication(@PathParam("app") String applicationId, JsonElement json) {
        accessControl(Group.managers());
        atomic(() -> {
            getApplicationForClientId(applicationId).setActive();
        });
        return ok();
    }

    @DELETE
    @Path("/{app}")
    public Response delete(@PathParam("app") String applicationId) {
        verifyAndGetRequestAuthor(applicationId);
        atomic(() -> {
            getApplicationForClientId(applicationId).setDeleted();
        });
        return ok();
    }

    @GET
    @Path("/{app}/logo")
    public Response logo(@PathParam("app") String applicationId, @HeaderParam("If-None-Match") String ifNoneMatch) {
        ExternalApplication app = getApplicationForClientId(applicationId);
        if (app != null && app.getLogo() != null) {
            EntityTag etag = buildETag(app);
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified(etag).build();
            }
            return Response.ok(Base64.getDecoder().decode(app.getLogo()), "image/jpeg").tag(etag).build();
        } else {
            try (InputStream placeholder = getClass().getResourceAsStream("/noapplication.png")) {
                return Response.ok(ByteStreams.toByteArray(placeholder), "image/png").build();
            } catch (IOException e) {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
    }

    private EntityTag buildETag(ExternalApplication instance) {
        return EntityTag.valueOf("W/\"" + instance.getLogo().length + "-" + instance.getExternalId() + "\"");
    }
}
