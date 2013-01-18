package pt.ist.bennu.dispatch;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.bennu.dispatch.model.ApplicationInfo;

import com.google.gson.JsonArray;

@Path("/apps")
public class AppServer {
	private static Set<ApplicationInfo> apps = new HashSet<>();

	static void registerApp(ApplicationInfo application) {
		apps.add(application);
	}

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listApps() {
		JsonArray array = new JsonArray();
		for (ApplicationInfo application : apps) {
			array.add(application.json());
		}
		return Response.ok(array.toString()).build();
	}
}
