package pt.ist.bennu.core.rest;

import java.util.Collection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.fenixframework.DomainObject;

public abstract class DomainObjectResource<T extends DomainObject> extends BennuRestResource {

    public abstract Collection<T> all();

    public abstract String collectionKey();

    public abstract Class<T> type();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String view() {
        return view(all(), collectionKey());
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String view(@PathParam("oid") String oid) {
        return view(readDomainObject(oid));
    }

    @PUT
    @Path("{oid}")
    public String update(@PathParam("oid") String oid, @FormParam("model") String jsonData) {
        return view(update(jsonData, readDomainObject(oid)));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@FormParam("model") String jsonData) {
        return view(create(jsonData, type()));
    }

}
