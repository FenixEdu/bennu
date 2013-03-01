package pt.ist.bennu.portal.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.bennu.portal.domain.exception.MenuNotAvailableException;
import pt.ist.bennu.service.Service;

@Path("menu")
public class MenuResource extends PortalResource {

    @Path("{hostname: [a-zA-Z0-9\\.]+}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String menu(@PathParam("hostname") final String hostname) {
        return view(getVirtualHost(hostname).getMenu());
    }

    @Path("{hostname: [a-zA-Z0-9\\.]+}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@PathParam("hostname") final String hostname, @FormParam("model") final String jsonData) {
        final VirtualHost virtualHost = innerCreate(hostname, jsonData);
        return view(virtualHost.getMenu());
    }

    @Service
    public VirtualHost innerCreate(final String hostname, final String jsonData) {
        final VirtualHost virtualHost = getVirtualHost(hostname);
        if (virtualHost.hasMenu()) {
            update(jsonData, virtualHost);
        } else {
            virtualHost.setMenu(create(jsonData, MenuItem.class));
        }
        return virtualHost;
    }

    @Path("{hostname: [a-zA-Z0-9\\.]+}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@PathParam("hostname") final String hostname, @FormParam("model") final String jsonData) {
        final VirtualHost virtualHost = innerUpdate(hostname, jsonData);
        return view(virtualHost.getMenu());
    }

    @Service
    public VirtualHost innerUpdate(final String hostname, final String jsonData) {
        final VirtualHost virtualHost = getVirtualHost(hostname);
        if (virtualHost.hasMenu()) {
            update(jsonData, virtualHost.getMenu());
        } else {
            throw new MenuNotAvailableException();
        }
        return virtualHost;
    }
}
