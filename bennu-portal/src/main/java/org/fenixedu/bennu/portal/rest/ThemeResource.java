package org.fenixedu.bennu.portal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.rest.JsonAwareResource;
import org.fenixedu.bennu.portal.servlet.PortalInitializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("themes")
public class ThemeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String themes() {
        JsonObject obj = new JsonObject();
        JsonArray themes = new JsonArray();
        for (String theme : PortalInitializer.getThemes()) {
            JsonObject themeJson = new JsonObject();
            themeJson.addProperty("name", theme);
            themes.add(themeJson);
        }
        obj.add("themes", themes);
        return JsonAwareResource.toJson(obj);
    }
}