package pt.ist.bennu.core.rest;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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
import pt.ist.fenixframework.pstm.AbstractDomainObject;

public abstract class DomainObjectResource<T extends AbstractDomainObject> extends BennuRestResource {

    private static final Logger LOG = LoggerFactory.getLogger("RestAdapters");

    public abstract Collection<T> all();

    public abstract String collectionKey();

    public abstract Class<T> type();

    public abstract boolean delete(T obj);

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

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@PathParam("oid") String oid) {
//        final T readDomainObject = readDomainObject(oid);
//        final String jsonRep = view(readDomainObject);
//        try {
//            final Method method = readDomainObject.getClass().getMethod(getDeleteMethodName(), null);
//            method.setAccessible(true);
//            method.invoke(readDomainObject, null);
//        } catch (Exception e) {
//            throw BennuCoreDomainException.errorOnDeleteDomainObject(e.getLocalizedMessage());
//        }
//        return jsonRep;

        final T readDomainObject = readDomainObject(oid);
        final String jsonRep = view(readDomainObject);
        if (!delete(readDomainObject)) {
            throw BennuCoreDomainException.errorOnDeleteDomainObject();
        }
        LOG.info("Object {} was deleted: {}", oid, jsonRep);
        return jsonRep;
    }
}
