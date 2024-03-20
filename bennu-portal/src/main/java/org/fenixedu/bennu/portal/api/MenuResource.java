package org.fenixedu.bennu.portal.api;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.api.json.MenuItemAdapter;
import org.fenixedu.bennu.portal.api.json.SupportConfigurationAdapter;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.SupportConfiguration;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

@Path("/bennu-portal/menu")
public class MenuResource extends BennuRestResource {

    @Path("{oid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement getMenu(@PathParam("oid") final String menuOid) {
        accessControl(Group.managers());
        return viewMenu(getMenuItem(menuOid));
    }

    private JsonElement viewMenu(MenuItem menuItem) {
        return view(menuItem, MenuItemAdapter.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement createMenu(final JsonElement jsonData) {
        accessControl(Group.managers());
        return viewMenu(create(jsonData, MenuContainer.class, MenuItemAdapter.class));
    }

    @POST
    @Path("/sub-root")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement createSubRoot(final JsonObject json) {
        accessControl(Group.managers());
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
    public JsonElement updateMenu(final JsonElement jsonData, @PathParam("oid") final String oid) {
        accessControl(Group.managers());
        return viewMenu(update(jsonData, getMenuItem(oid), MenuItemAdapter.class));
    }

    @Path("{oid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement deleteMenu(@PathParam("oid") final String menuOid) {
        accessControl(Group.managers());
        final MenuItem menuItem = getMenuItem(menuOid);
        final JsonElement rsp = viewMenu(menuItem);
        menuItem.delete();
        return rsp;
    }

    @POST
    @Path("/order")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reorderItems(final JsonObject object) {
        accessControl(Group.managers());
        reorder(object);
        return ok();
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
    public JsonElement listApps() {
        accessControl(Group.managers());
        return view(ApplicationRegistry.availableApplications());
    }

    @POST
    @Path("/applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonElement installApp(JsonObject obj) {
        accessControl(Group.managers());
        MenuContainer container = readDomainObject(obj.get("root").getAsString());
        Application app = ApplicationRegistry.getAppByKey(obj.get("key").getAsString());
        if (app == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return viewMenu(doInstall(app, container));
    }

    @POST
    @Path("/functionalities")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonElement installFunctionality(JsonObject obj) {
        accessControl(Group.managers());
        MenuContainer container = readDomainObject(obj.get("root").getAsString());
        Application app = ApplicationRegistry.getAppByKey(obj.get("appKey").getAsString());
        if (app == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        Optional<MenuFunctionality> functionality = app.getFunctionalities().stream().filter(
                f -> f.getProvider().equals(obj.get("provider").getAsString()) && f.getKey().equals(obj.get("key").getAsString()))
                .findAny().map(f -> doInstall(container, (Functionality) f));

        if (functionality.isPresent()) {
            return viewMenu(functionality.get());
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @POST
    @Path("{oid}/support/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement addSupport(JsonElement jsonData, @PathParam("oid") final String oid) {
        accessControl(Group.managers());
        final MenuItem menuItem = getMenuItem(oid);
        createSupport(menuItem, jsonData);
        return viewMenu(menuItem);
    }

    @Atomic
    private void createSupport(MenuItem menuItem, JsonElement jsonData) {
        SupportConfiguration supportConfiguration =
                create(jsonData, SupportConfiguration.class, SupportConfigurationAdapter.class);
        menuItem.setSupport(supportConfiguration);
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
