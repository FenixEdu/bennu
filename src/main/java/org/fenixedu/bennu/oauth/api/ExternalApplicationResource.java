package org.fenixedu.bennu.oauth.api;

import static pt.ist.fenixframework.FenixFramework.atomic;

import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.joda.time.DateTime;

@Path("/bennu-oauth")
public class ExternalApplicationResource extends BennuRestResource {

    @GET
    @OAuthEndpoint("info")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/applications")
    public String myApplications() {
        return view(verifyAndGetRequestAuthor().getOwnedApplicationSet().stream().filter(p -> p.isActive())
                .sorted((a1, a2) -> a1.getName().compareTo(a2.getName())));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all-applications")
    public String allApplications() {
        accessControl("#managers");
        return view(Bennu.getInstance().getApplicationsSet());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/applications")
    public String createApplication(String json) {
        verifyAndGetRequestAuthor();
        return view(create(json, ExternalApplication.class));
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/applications/{app}")
    public String updateApplication(@PathParam("app") ExternalApplication application, String json) {
        verifyAndGetRequestAuthor();
        return view(update(json, application));
    }

    @DELETE
    @Path("/applications/{app}")
    public Response delete(@PathParam("app") ExternalApplication app) {
        verifyAndGetRequestAuthor();
        atomic(() -> {
            app.setDeleted();
        });
        return Response.ok().build();
    }

    @GET
    @Path("/applications/{app}/logo")
    public Response logo(@PathParam("app") ExternalApplication app, @HeaderParam("If-None-Match") String ifNoneMatch) {
        if (app != null && app.getLogo() != null) {
            EntityTag etag = buildETag(app);
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified(etag).build();
            }
            return Response.ok(Base64.getDecoder().decode(app.getLogo()), "image/jpeg").cacheControl(CACHE_CONTROL)
                    .expires(DateTime.now().plusHours(12).toDate()).tag(etag).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    private static final CacheControl CACHE_CONTROL = CacheControl.valueOf("max-age=43200");

    private EntityTag buildETag(ExternalApplication instance) {
        return EntityTag.valueOf("W/\"" + instance.getLogo().length + "-" + instance.getExternalId() + "\"");
    }
}
