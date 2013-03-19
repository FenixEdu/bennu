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
import pt.ist.bennu.portal.rest.json.HostMenuViewer;

@Path("hostmenu")
public class HostMenuResource extends BennuRestResource {

    @Path("{hostname: [a-zA-Z0-9\\.]+}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String menu(@PathParam("hostname") final String hostname) {
        return view(getVirtualHost(hostname), HostMenuViewer.class);
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
