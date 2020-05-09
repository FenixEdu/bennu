package org.fenixedu.bennu.io.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.io.domain.FileStorageConfiguration;
import org.fenixedu.bennu.io.domain.FileSupport;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonElement;

@Path("/bennu-io/storage/config")
public class StorageConfigurationResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement all() {
        accessControl(Group.managers());
        createMissingConfigurations();
        return view(FileSupport.getInstance().getConfigurationSet(), "storageConfigurations");
    }

    @Atomic
    private void createMissingConfigurations() {
        FileStorageConfiguration.createMissingStorageConfigurations();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement post(final JsonElement json) {
        accessControl(Group.managers());
        create(json, FileStorageConfiguration.class);
        return all();
    }

}
