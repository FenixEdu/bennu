package pt.ist.bennu.core.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.fenixframework.core.AbstractDomainObject;

public abstract class DomainObjectResource<T extends AbstractDomainObject> extends BennuRestResource {

    private static final Logger LOG = LoggerFactory.getLogger("RestAdapters");

    public abstract Collection<T> all();

    public abstract String collectionKey();

    public abstract Class<T> type();

    public abstract boolean delete(T obj);

    public abstract String getAccessExpression();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String view() {
        accessControl(getAccessExpression());
        return view(all(), collectionKey());
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String view(@PathParam("oid") String oid) {
        accessControl(getAccessExpression());
        return view(readDomainObject(oid));
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(String jsonData, @PathParam("oid") String oid) {
        accessControl(getAccessExpression());
        return view(update(jsonData, readDomainObject(oid)));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String jsonData) {
        accessControl(getAccessExpression());
        return view(create(jsonData, type()));
    }

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@PathParam("oid") String oid) {
        accessControl(getAccessExpression());
        final T readDomainObject = readDomainObject(oid);
        final String jsonRep = view(readDomainObject);
        if (!delete(readDomainObject)) {
            throw BennuCoreDomainException.errorOnDeleteDomainObject();
        }
        LOG.trace("Object {} was deleted: {}", oid, jsonRep);
        return jsonRep;
    }
}
