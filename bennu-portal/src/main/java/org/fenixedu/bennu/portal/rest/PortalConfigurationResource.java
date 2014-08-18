package org.fenixedu.bennu.portal.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.joda.time.DateTime;

@Path("/bennu-portal/configuration")
public class PortalConfigurationResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String viewConfig() {
        accessControl("#managers");
        return view(PortalConfiguration.getInstance());
    }

    private static final CacheControl CACHE_CONTROL = CacheControl.valueOf("max-age=43200");

    @GET
    @Path("logo")
    public Response logo(@HeaderParam("If-None-Match") String ifNoneMatch) {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getLogo() != null) {
            EntityTag etag = buildETag(instance);
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified(etag).build();
            }
            return Response.ok(instance.getLogo(), instance.getLogoType()).cacheControl(CACHE_CONTROL)
                    .expires(DateTime.now().plusHours(12).toDate()).tag(etag).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    private EntityTag buildETag(PortalConfiguration instance) {
        return EntityTag.valueOf("W/\"" + instance.getLogo().length + "-" + instance.getExternalId() + "\"");
    }

    @GET
    @Path("favicon")
    public Response favicon() {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getFavicon() != null) {
            return Response.ok(instance.getFavicon(), instance.getFaviconType()).cacheControl(CACHE_CONTROL)
                    .expires(DateTime.now().plusHours(12).toDate()).build();
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
