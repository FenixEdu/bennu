package org.fenixedu.bennu.portal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.json.adapters.AuthenticatedUserViewer;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.rest.json.PortalMenuViewer;

import com.google.gson.JsonObject;

@Path("data")
public class PortalDataResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMenu() {
        final JsonObject hostMenuView = new JsonObject();
        merge(hostMenuView, getBuilder().view(PortalConfiguration.getInstance(), PortalMenuViewer.class).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(getCasConfigContext()).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(null, Void.class, AuthenticatedUserViewer.class).getAsJsonObject());
        return toJson(hostMenuView);
    }
}
