package pt.ist.bennu.portal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.core.rest.json.UserSessionViewer;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.bennu.portal.domain.PortalConfiguration;

import com.google.gson.JsonObject;

@Path("data")
public class PortalDataResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMenu() {
        final JsonObject hostMenuView = new JsonObject();
        JsonObject asJsonObject = getBuilder().view(PortalConfiguration.getInstance()).getAsJsonObject();
        asJsonObject.add("menu", getBuilder().view(PortalConfiguration.getInstance().getMenu()));
        merge(hostMenuView, asJsonObject);
        merge(hostMenuView, getBuilder().view(getCasConfigContext()).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)
                .getAsJsonObject());
        return toJson(hostMenuView);
    }
}
