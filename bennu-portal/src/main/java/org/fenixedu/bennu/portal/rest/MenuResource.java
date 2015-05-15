package org.fenixedu.bennu.portal.rest;

import java.util.Map.Entry;
import java.util.Optional;

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
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.rest.json.MenuItemAdapter;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/bennu-portal/menu")
public class MenuResource extends BennuRestResource {

    @Path("{oid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMenu(@PathParam("oid") final String menuOid) {
        accessControl("#managers");
        return viewMenu(getMenuItem(menuOid));
    }

    private String viewMenu(MenuItem menuItem) {
        return view(menuItem, MenuItemAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createMenu(final String jsonData) {
        accessControl("#managers");
        return viewMenu(create(jsonData, MenuContainer.class, MenuItemAdapter.class));
    }

    @POST
    @Path("/sub-root")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createSubRoot(final String jsonData) {
        accessControl("#managers");
        JsonObject json = new JsonParser().parse(jsonData).getAsJsonObject();
        String key = json.get("key").getAsString();
        LocalizedString title = LocalizedString.fromJson(json.get("title"));
        LocalizedString description = LocalizedString.fromJson(json.get("description"));
        return viewMenu(createSubRoot(key, title, description));
    }

    @Atomic(mode = TxMode.WRITE)
    private MenuItem createSubRoot(String key, LocalizedString title, LocalizedString description) {
        return MenuContainer.createSubRoot(key, title, description);
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateMenu(final String jsonData, @PathParam("oid") final String oid) {
        accessControl("#managers");
        return viewMenu(update(jsonData, getMenuItem(oid), MenuItemAdapter.class));
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

    @POST
    @Path("/order")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reorderItems(final String jsonData) {
        accessControl("#managers");
        JsonObject object = new JsonParser().parse(jsonData).getAsJsonObject();
        reorder(object);
        return Response.ok().build();
    }

    @Atomic
    private void reorder(JsonObject object) {
        for (Entry<String, JsonElement> entry : object.entrySet()) {
            MenuItem item = FenixFramework.getDomainObject(entry.getKey());
            item.setOrd(entry.getValue().getAsInt());
        }
    }

    @GET
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    public String listApps() {
        accessControl("#managers");
        return view(ApplicationRegistry.availableApplications());
    }

    @POST
    @Path("/applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response installApp(String json) {
        accessControl("#managers");
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        MenuContainer container = readDomainObject(obj.get("root").getAsString());
        Application app = ApplicationRegistry.getAppByKey(obj.get("key").getAsString());
        if (app == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(viewMenu(doInstall(app, container))).build();
    }

    @POST
    @Path("/functionalities")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response installFunctionality(String json) {
        accessControl("#managers");
        JsonObject obj = parse(json).getAsJsonObject();
        MenuContainer container = readDomainObject(obj.get("root").getAsString());
        Application app = ApplicationRegistry.getAppByKey(obj.get("appKey").getAsString());
        if (app == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        Optional<MenuFunctionality> functionality =
                app.getFunctionalities()
                        .stream()
                        .filter(f -> f.getProvider().equals(obj.get("provider").getAsString())
                                && f.getKey().equals(obj.get("key").getAsString())).findAny().map(f -> doInstall(container, f));
        if (functionality.isPresent()) {
            return Response.ok(viewMenu(functionality.get())).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private MenuFunctionality doInstall(MenuContainer container, Functionality functionality) {
        return new MenuFunctionality(container, functionality);
    }

    @Atomic(mode = TxMode.WRITE)
    private MenuContainer doInstall(Application app, MenuContainer container) {
        return new MenuContainer(container, app);
    }

    private MenuItem getMenuItem(String oid) {
        return readDomainObject(oid);
    }
}
