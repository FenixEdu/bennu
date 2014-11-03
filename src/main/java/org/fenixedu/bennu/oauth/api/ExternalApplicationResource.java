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
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;

@Path("/bennu-oauth")
public class ExternalApplicationResource extends BennuRestResource {

    @GET
    @OAuthEndpoint("info")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/applications")
    public String myApplications() {
        return view(verifyAndGetRequestAuthor().getOwnedApplicationSet().stream().filter(p -> p.isActive()));
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
}
