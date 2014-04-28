package org.fenixedu.bennu.portal.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

@Path("/bennu-portal/configuration")
public class PortalConfigurationResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String viewConfig() {
        accessControl("#managers");
        return view(PortalConfiguration.getInstance());
    }

    @GET
    @Path("logo")
    public Response logo() {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getLogo() != null) {
            return Response.ok(instance.getLogo(), instance.getLogoType()).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("favicon")
    public Response favicon() {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getFavicon() != null) {
            return Response.ok(instance.getFavicon(), instance.getFaviconType()).build();
        }
        return Response.status(Status.NOT_FOUND).build();
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
