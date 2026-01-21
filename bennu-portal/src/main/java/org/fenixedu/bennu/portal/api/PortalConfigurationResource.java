package org.fenixedu.bennu.portal.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import com.google.gson.JsonElement;

@Path("/bennu-portal/configuration")
public class PortalConfigurationResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement viewConfig() {
        accessControl(Group.managers());
        return view(PortalConfiguration.getInstance());
    }

    @GET
    @Path("logo")
    public Response logo(@HeaderParam("If-None-Match") String ifNoneMatch) {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getLogo() != null) {
            EntityTag etag = buildETag(instance);
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified(etag).build();
            }
            return Response.ok(instance.getLogo(), instance.getLogoType())
                    .header(HttpHeaders.CACHE_CONTROL, CoreConfiguration.getConfiguration().staticCacheControl()).tag(etag)
                    .build();
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("logomark")
    public Response logomark(@HeaderParam("If-None-Match") String ifNoneMatch) {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getLogomark() != null) {
            EntityTag etag = buildETag(instance);
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified(etag).build();
            }
            return Response.ok(instance.getLogomark(), instance.getLogomarkType())
                    .header(HttpHeaders.CACHE_CONTROL, CoreConfiguration.getConfiguration().staticCacheControl()).tag(etag)
                    .build();
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    private EntityTag buildETag(PortalConfiguration instance) {
        return EntityTag.valueOf("W/\"" + instance.getLogo().length + "-" + instance.getExternalId() + "\"");
    }

    @GET
    @Path("favicon")
    public Response favicon() {
        final PortalConfiguration instance = PortalConfiguration.getInstance();
        if (instance != null && instance.getFavicon() != null) {
            return Response.ok(instance.getFavicon(), instance.getFaviconType())
                    .header(HttpHeaders.CACHE_CONTROL, CoreConfiguration.getConfiguration().staticCacheControl()).build();
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement updateConfig(JsonElement jsonData, @PathParam("oid") String oid) {
        accessControl(Group.managers());
        return view(update(jsonData, readDomainObject(oid)));
    }

}
