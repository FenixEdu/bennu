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
				JsonObject obj = new JsonObject();

				ToolkitComponent annotation = component.getClass()
						.getAnnotation(ToolkitComponent.class);

				obj.addProperty("key", annotation.key());
				obj.addProperty("name", annotation.name());
				obj.addProperty("description", annotation.description());
				JsonArray array2 = new JsonArray();

				for (String file : annotation.editorFiles()) {
					JsonPrimitive element = new JsonPrimitive(file);
					array2.add(element);
				}

				obj.add("files", array2);
				cachedResponse.add(obj);
			}

		}
		return Response.ok(toJson(cachedResponse)).build();
	}
}
