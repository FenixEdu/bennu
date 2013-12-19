package org.fenixedu.bennu.portal.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.domain.MenuItem;

@Path("menu")
public class MenuResource extends BennuRestResource {

    @Path("{oid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMenu(@PathParam("oid") final String menuOid) {
        accessControl("#managers");
        return viewMenu(getMenuItem(menuOid));
    }

    private String viewMenu(MenuItem menuItem) {
        return view(menuItem);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createMenu(final String jsonData) {
        accessControl("#managers");
        return view(create(jsonData, MenuItem.class));
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateMenu(final String jsonData, @PathParam("oid") final String oid) {
        accessControl("#managers");
        return view(update(jsonData, getMenuItem(oid)));
    }

    @Path("{oid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteMenu(@PathParam("oid") final String menuOid) {
        accessControl("#managers");
        final MenuItem menuItem = getMenuItem(menuOid);
        final String rsp = viewMenu(menuItem);
        menuItem.delete();
        return rsp;
    }

    private MenuItem getMenuItem(String oid) {
        return readDomainObject(oid);
    }
}
