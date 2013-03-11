package pt.ist.bennu.dispatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.dispatch.model.ApplicationInfo;
import pt.ist.bennu.dispatch.model.FunctionalityInfo;

import com.google.gson.JsonArray;

@Path("apps")
public class AppServer {
    private static Set<ApplicationInfo> apps = new HashSet<>();
    private static Map<String, FunctionalityInfo> pathFunctionalityMap = new HashMap<>();

    static void registerApp(ApplicationInfo application) {
        for (FunctionalityInfo func : application.getFunctionalities()) {
            pathFunctionalityMap.put(func.getPath(), func);
        }
        pathFunctionalityMap.put(application.getPath(), application);
        apps.add(application);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listApps() {
        JsonArray array = new JsonArray();
        for (ApplicationInfo application : apps) {
            array.add(application.json());
        }
        return Response.ok(array.toString()).build();
    }

    @GET
    @Path("{path:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listFunctionality(@PathParam("path") String path) {
        final FunctionalityInfo abstractFunctionalityInfo = pathFunctionalityMap.get(path);
        if (abstractFunctionalityInfo != null) {
            return abstractFunctionalityInfo.json().toString();
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }
}
