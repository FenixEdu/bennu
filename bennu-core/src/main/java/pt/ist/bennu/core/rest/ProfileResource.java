package pt.ist.bennu.core.rest;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.LoggedGroup;
import pt.ist.bennu.core.i18n.I18N;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Path("/profile")
public class ProfileResource extends BennuRestResource {
    public static class ProfileJsonViewer implements JsonViewer<User> {
        @Override
        public JsonElement view(User user, JsonBuilder ctx) {
            JsonObject object;
            if (user != null) {
                object = ctx.view(user).getAsJsonObject();
                object.add("groups", ctx.view(user.accessibleGroups()));
            } else {
                object = new JsonObject();
            }
            object.addProperty("locale", I18N.getLocale().toLanguageTag());
            return object;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile() {
        return Response.ok(view(Authenticate.getUser(), User.class, ProfileJsonViewer.class)).build();
    }

    @POST
    @Path("/locale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeLocale(String localeTag) {
        I18N.setLocale(request.getSession(true), Locale.forLanguageTag(localeTag));
        return Response.ok(view(Authenticate.getUser(), ProfileJsonViewer.class)).build();
    }

    @POST
    @Path("/preferred-locale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePreferredLocale(String localeTag) {
        accessControl(LoggedGroup.getInstance());
        Locale locale = Locale.forLanguageTag(localeTag);
        Authenticate.getUser().setPreferredLocale(locale);
        I18N.setLocale(request.getSession(true), locale);
        return Response.ok(view(Authenticate.getUser(), ProfileJsonViewer.class)).build();
    }
}