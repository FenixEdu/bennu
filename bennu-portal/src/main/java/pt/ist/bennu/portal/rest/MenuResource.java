package pt.ist.bennu.portal.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.bennu.service.Service;

@Path("menu")
public class MenuResource extends BennuRestResource {

    @Path("{oid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMenu(@PathParam("oid") final String menuOid) {
        return viewMenu((MenuItem) readDomainObject(menuOid));
    }

    private String viewMenu(MenuItem menuItem) {
        return view(menuItem);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@FormParam("model") final String jsonData) {
        return innerCreate(jsonData);
    }

    @Service
    public String innerCreate(final String jsonData) {
        return view(create(jsonData, MenuItem.class));
    }

    @Path("{oid}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@PathParam("oid") final String oid, @FormParam("model") final String jsonData) {
        return view(innerUpdate((MenuItem) readDomainObject(oid), jsonData));
    }

    @Service
    public MenuItem innerUpdate(final MenuItem menuItem, final String jsonData) {
        return update(jsonData, menuItem);
    }

    @Path("{oid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteMenu(@PathParam("oid") final String menuOid) {
        final MenuItem menuItem = (MenuItem) readDomainObject(menuOid);
        final String rsp = viewMenu(menuItem);
        menuItem.delete();
        return rsp;
    }
}
