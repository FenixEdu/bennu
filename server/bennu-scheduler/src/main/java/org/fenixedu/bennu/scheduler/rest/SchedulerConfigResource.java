package org.fenixedu.bennu.scheduler.rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.io.domain.LocalFileSystemStorage;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Path("/bennu-scheduler/config")
public class SchedulerConfigResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getLoggingStorage() {
        accessControl(Group.managers());
        return view(SchedulerSystem.getInstance());
    }

    @PUT
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String changeLoggingStorage(@PathParam("oid") String loggingStorageExternalId) {
        accessControl(Group.managers());
        innerSetLoggingStorage(loggingStorageExternalId);
        return getLoggingStorage();
    }

    @Atomic(mode = TxMode.WRITE)
    public void innerSetLoggingStorage(String loggingStorageExternalId) {
        LocalFileSystemStorage storage = readDomainObject(loggingStorageExternalId);
        SchedulerSystem.getInstance().setLoggingStorage(storage);
    }

}
