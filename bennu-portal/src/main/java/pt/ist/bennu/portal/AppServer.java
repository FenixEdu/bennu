package pt.ist.bennu.portal;

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

import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.rest.json.JsonAwareResource;
import pt.ist.bennu.portal.domain.ApplicationInfo;
import pt.ist.bennu.portal.domain.FunctionalityInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("apps")
public class AppServer {
    private static Logger LOG = LoggerFactory.getLogger(AppServer.class);
    private static Set<ApplicationInfo> apps = new HashSet<>();
    private static Map<String, FunctionalityInfo> pathFunctionalityMap = new HashMap<>();

    static void registerApp(ApplicationInfo application) {
        mapFunctionality(application);
        for (FunctionalityInfo func : application.getFunctionalities()) {
            mapFunctionality(func);
        }
        apps.add(application);
    }

    public static void mapFunctionality(FunctionalityInfo functionality) {
        pathFunctionalityMap.put(functionality.getPath(), functionality);
        LOG.info("register functionality {}", functionality.getPath());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listApps() {
        JsonArray appsArray = new JsonArray();
        for (ApplicationInfo application : apps) {
            appsArray.add(application.json());
        }
        JsonObject appsJson = new JsonObject();
        appsJson.add("apps", appsArray);
        return Response.ok(JsonAwareResource.toJson(appsJson)).build();
    }

    @GET
    @Path("{path:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listFunctionality(@PathParam("path") String path) {
        final FunctionalityInfo abstractFunctionalityInfo = pathFunctionalityMap.get(path);
        if (abstractFunctionalityInfo != null) {
            return JsonAwareResource.toJson(abstractFunctionalityInfo.json());
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    public static LocalizedString getTitle(String path) {
        final FunctionalityInfo functionalityInfo = pathFunctionalityMap.get(path);
        if (functionalityInfo != null) {
            return functionalityInfo.getTitle();
        }
        return null;
    }

    public static LocalizedString getDescription(String path) {
        final FunctionalityInfo functionalityInfo = pathFunctionalityMap.get(path);
        if (functionalityInfo != null) {
            return functionalityInfo.getDescription();
        }
        return null;
    }

    public static Boolean hasFunctionality(String path) {
        return getFunctionalityInfo(path) != null;
    }

    public static FunctionalityInfo getFunctionalityInfo(String path) {
        return pathFunctionalityMap.get(path);
    }
}
