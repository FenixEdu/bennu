package org.fenixedu.bennu.io.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.io.domain.*;
import pt.ist.fenixframework.Atomic;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/bennu-io/storage")
public class FileStorageResource extends BennuRestResource {

    @POST
    @Path("/default/{storage}")
    public JsonElement setDefault(final @PathParam("storage") String storageId) {
        accessControl(Group.managers());
        innerSetDefault(this.<FileStorage> readDomainObject(storageId));
        return all();
    }

    @Atomic
    private void innerSetDefault(final FileStorage storage) {
        FileSupport.getInstance().setDefaultStorage(storage);
    }

    @POST
    @Path("/domain/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement createDomainStorage(final @PathParam("name") String name) {
        accessControl(Group.managers());
        return view(createDomainStorageService(name));
    }

    @POST
    @Path("/lfs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement createLFSStorage(final JsonElement json) {
        accessControl(Group.managers());
        return view(create(json, LocalFileSystemStorage.class));
    }

    @POST
    @Path("/drive")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement createLDriveStorage(final JsonElement json) {
        accessControl(Group.managers());
        return view(create(json, DriveAPIStorage.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement all() {
        accessControl(Group.managers());
        return view(FileSupport.getInstance().getFileStorageSet(), "storages");
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject fileCount() {
        accessControl(Group.managers());
        final JsonObject json = new JsonObject();
        for (final FileStorage store : FileSupport.getInstance().getFileStorageSet()) {
            json.addProperty(store.getExternalId(), store.getFileSet().size());
        }
        return json;
    }

    @Atomic
    private DomainStorage createDomainStorageService(final String name) {
        return FileStorage.createNewDomainStorage(name);
    }

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement delete(final @PathParam("oid") String storageOid) {
        accessControl(Group.managers());
        final FileStorage fileStorage = (FileStorage) readDomainObject(storageOid);
        final JsonElement response = view(fileStorage);
        final Boolean deleteStorage = deleteStorage(fileStorage);
        if (deleteStorage) {
            return response;
        }
        throw new WebApplicationException(Status.NO_CONTENT);
    }

    @Atomic
    private Boolean deleteStorage(final FileStorage fileStorage) {
        return fileStorage.delete();
    }

    @PUT
    @Path("/convert/{oid}")
    public Response convertFileStorage(final @PathParam("oid") String fileStorageOid) {
        accessControl(Group.managers());
        GenericFile.convertFileStorages((FileStorage) readDomainObject(fileStorageOid));
        return ok();
    }

}
