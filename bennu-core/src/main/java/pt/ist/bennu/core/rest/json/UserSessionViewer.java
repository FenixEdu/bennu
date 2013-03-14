package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.i18n.I18N;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(UserSession.class)
public class UserSessionViewer implements JsonViewer<UserSession> {
    @Override
    public JsonElement view(UserSession user, JsonBuilder ctx) {
        JsonObject object;
        if (user != null) {
            object = ctx.view(user.getUser()).getAsJsonObject();
            object.add("groups", ctx.view(user.accessibleGroups()));
        } else {
            object = new JsonObject();
        }
        object.add("locale", ctx.view(I18N.getLocale()));
        JsonArray locales = ctx.view(VirtualHost.getVirtualHostForThread().getSupportedLanguages().getLocales()).getAsJsonArray();
        boolean found = false;
        for (JsonElement locale : locales) {
            if (locale.getAsJsonObject().get("tag").getAsString().equals(I18N.getLocale().toLanguageTag())) {
                found = true;
                locale.getAsJsonObject().addProperty("selected", true);
            }
        }
        if (!found) {
            JsonObject locale = ctx.view(I18N.getLocale()).getAsJsonObject();
            locale.addProperty("selected", true);
            locales.add(locale);
        }
        object.add("locales", locales);
        return object;
    }
}
