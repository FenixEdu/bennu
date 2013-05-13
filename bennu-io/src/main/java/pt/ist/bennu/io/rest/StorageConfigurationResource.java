package pt.ist.bennu.io.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.io.domain.FileStorageConfiguration;
import pt.ist.bennu.io.domain.FileSupport;
import pt.ist.fenixframework.Atomic;

@Path("/storage/config")
public class StorageConfigurationResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String all() {
        accessControl("#managers");
        createMissingConfigurations();
        return view(FileSupport.getInstance().getFileStorageConfigurationsSet(), "storageConfigurations");
    }

    @Atomic
    private void createMissingConfigurations() {
        FileStorageConfiguration.createMissingStorageConfigurations();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post(@FormParam("model") String json) {
        accessControl("#managers");
        create(json, FileStorageConfiguration.class);
        return all();
    }

}
