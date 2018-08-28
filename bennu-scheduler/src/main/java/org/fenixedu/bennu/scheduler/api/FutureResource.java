package org.fenixedu.bennu.scheduler.api;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.future.FutureSystem;
import org.fenixedu.bennu.scheduler.future.PersistentFuture;

import com.google.gson.JsonElement;

import pt.ist.fenixframework.Atomic;

@Path("/bennu-scheduler/future")
public class FutureResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement latest(@QueryParam("count") @DefaultValue("20") int max, @QueryParam("skip") @DefaultValue("0") int skip) {
        accessControl(Group.managers());
        return view(FutureSystem.getInstance().getPersistentFutureSet().stream().skip(skip).limit(max));
    }

    @GET
    @Path("cancel/{futureId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelFuture(@PathParam("futureId") String futureId) {
        accessControl(Group.managers());
        final PersistentFuture pf = FutureSystem.getPersistentFuture(futureId);
        cancelFuture(pf);
        return ok();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private boolean cancelFuture(PersistentFuture pf) {
        return pf.cancel();
    }

}