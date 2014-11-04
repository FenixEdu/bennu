package org.fenixedu.bennu.oauth.api;

import static pt.ist.fenixframework.FenixFramework.atomic;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

@Path("/bennu-oauth/scopes")
public class ExternalApplicationScopesResource extends BennuRestResource {

    @GET
    @OAuthEndpoint("info")
    @Produces(MediaType.APPLICATION_JSON)
    public String getScopes() {
        return view(Bennu.getInstance().getScopesSet().stream().sorted((a1, a2) -> a1.getName().compareTo(a2.getName())));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String createScope(String json) {
        verifyAndGetRequestAuthor();
        return view(create(json, ExternalApplicationScope.class));
    }

    @DELETE
    @Path("/{scope}")
    public Response delete(@PathParam("scope") ExternalApplicationScope scope) {
        accessControl("#managers");

        atomic(() -> {
            for (ExternalApplication externalApplication : Bennu.getInstance().getApplicationsSet()) {
                externalApplication.removeScope(scope);
            }
            Bennu.getInstance().removeScopes(scope);
        });
        return Response.ok().build();
    }

}
