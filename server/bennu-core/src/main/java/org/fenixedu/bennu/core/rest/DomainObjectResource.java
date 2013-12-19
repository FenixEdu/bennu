package org.fenixedu.bennu.core.rest;

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

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.core.AbstractDomainObject;

public abstract class DomainObjectResource<T extends AbstractDomainObject> extends BennuRestResource {

    private static final Logger LOG = LoggerFactory.getLogger("RestAdapters");

    public abstract Collection<T> all();

    public abstract String collectionKey();

    public abstract Class<T> type();

    public abstract boolean delete(T obj);

    public abstract String getAccessExpression();

    public boolean canList() {
        return true;
    }

    public boolean canCreate() {
        return true;
    }

    public boolean canUpdate() {
        return true;
    }

    public boolean canView() {
        return true;
    }

    public boolean canDelete() {
        return true;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String view() {
        if (canList()) {
            accessControl(getAccessExpression());
            return view(all(), collectionKey());
        }
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String view(@PathParam("oid") String oid) {
        if (canView()) {
            accessControl(getAccessExpression());
            return view(readDomainObject(oid));
        }
        throw new UnsupportedOperationException();
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(String jsonData, @PathParam("oid") String oid) {
        if (canUpdate()) {
            accessControl(getAccessExpression());
            return view(update(jsonData, readDomainObject(oid)));
        }
        throw new UnsupportedOperationException();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String jsonData) {
        if (canCreate()) {
            accessControl(getAccessExpression());
            return view(create(jsonData, type()));
        }
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@PathParam("oid") String oid) {
        if (canDelete()) {
            accessControl(getAccessExpression());
            final T readDomainObject = readDomainObject(oid);
            final String jsonRep = view(readDomainObject);
            if (!delete(readDomainObject)) {
                throw BennuCoreDomainException.errorOnDeleteDomainObject();
            }
            LOG.trace("Object {} was deleted: {}", oid, jsonRep);
            return jsonRep;
        }
        throw new UnsupportedOperationException();
    }
}
