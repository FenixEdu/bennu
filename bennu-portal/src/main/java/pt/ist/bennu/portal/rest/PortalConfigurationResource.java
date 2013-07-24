package pt.ist.bennu.portal.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.portal.domain.PortalConfiguration;

@Path("configuration")
public class PortalConfigurationResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String viewConfig() {
        return view(PortalConfiguration.getInstance());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createConfig(final String json) {
        accessControl("#managers");
        return view(create(json, PortalConfiguration.class));
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateConfig(String jsonData, @PathParam("oid") String oid) {
        accessControl("#managers");
        return view(update(jsonData, readDomainObject(oid)));
    }
}
