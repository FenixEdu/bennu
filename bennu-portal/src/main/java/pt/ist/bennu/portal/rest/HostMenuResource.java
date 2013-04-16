package pt.ist.bennu.portal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.core.rest.json.UserSessionViewer;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.bennu.portal.rest.json.HostMenuViewer;

import com.google.gson.JsonObject;

@Path("hostmenu")
public class HostMenuResource extends BennuRestResource {

    @Path("{hostname: [a-zA-Z0-9\\.]+}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String menu(@PathParam("hostname") final String hostname) {
        final JsonObject hostMenuView = new JsonObject();
        merge(hostMenuView, getBuilder().view(getVirtualHost(hostname), HostMenuViewer.class).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(getCasConfigContext()).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)
                .getAsJsonObject());
        return toJson(hostMenuView);
    }

    private VirtualHost getVirtualHost(String hostname) {
        final VirtualHost virtualHost = Bennu.getInstance().getVirtualHost(hostname);
        if (virtualHost != null) {
            return virtualHost;
        } else {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }
}
