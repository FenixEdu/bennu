package org.fenixedu.bennu.toolkit.components;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.rest.BennuRestResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Path("/bennu-toolkit/")
public class ToolkitResources extends BennuRestResource {

    private JsonArray cachedResponse;

    @GET
    @Path("components")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response components() throws IOException {
        if (cachedResponse == null) {
            cachedResponse = new JsonArray();

            for (Component component : Component.getComponents()) {
                JsonObject json = new JsonObject();

                ToolkitComponent annotation = component.getClass().getAnnotation(ToolkitComponent.class);

                json.addProperty("key", annotation.key());
                json.addProperty("name", annotation.name());
                json.addProperty("description", annotation.description());
                json.addProperty("category", annotation.category());

                JsonArray fileArray = new JsonArray();

                for (String file : annotation.editorFiles()) {
                    JsonPrimitive element = new JsonPrimitive(file);
                    fileArray.add(element);
                }

                JsonArray resourceArray = new JsonArray();
                for (String resource : annotation.viewerFiles()) {
                    JsonPrimitive element = new JsonPrimitive(resource);
                    resourceArray.add(element);
                }

                json.add("files", fileArray);
                json.add("resources", resourceArray);
                cachedResponse.add(json);
            }
        }
        return Response.ok(toJson(cachedResponse)).build();
    }
}
