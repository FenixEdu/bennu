package pt.ist.bennu.portal.rest.json;

import org.apache.commons.codec.binary.Base64;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.rest.json.DomainObjectViewer;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonUpdater;
import pt.ist.bennu.json.JsonViewer;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.bennu.portal.domain.PortalConfiguration;
import pt.ist.dsi.commons.i18n.LocalizedString;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(PortalConfiguration.class)
public class PortalConfigurationAdapter implements JsonViewer<PortalConfiguration>, JsonUpdater<PortalConfiguration> {
    @Override
    public JsonElement view(PortalConfiguration configuration, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("id", configuration.getExternalId());
        object.add("applicationTitle", configuration.getApplicationTitle().json());
        object.add("htmlTitle", configuration.getHtmlTitle().json());
        object.add("applicationSubTitle", configuration.getApplicationSubTitle().json());
        object.add("applicationCopyright", configuration.getApplicationCopyright().json());
        object.addProperty("supportEmailAddress", configuration.getSupportEmailAddress());
        object.addProperty("systemEmailAddress", configuration.getSystemEmailAddress());
        object.addProperty("theme", configuration.getTheme());
        if (configuration.getLogo() != null) {
            object.addProperty("logo", Base64.encodeBase64String(configuration.getLogo()));
            object.addProperty("logoType", new String(configuration.getLogoType()));
        }
        object.add("menu", ctx.view(configuration.getMenu(), DomainObjectViewer.class));
        return object;
    }

    @Override
    public PortalConfiguration update(JsonElement json, PortalConfiguration configuration, JsonBuilder ctx) {
        JsonObject object = json.getAsJsonObject();
        if (object.has("applicationTitle")) {
            configuration.setApplicationTitle(LocalizedString.fromJson(object.get("applicationTitle")));
        }
        if (object.has("htmlTitle")) {
            configuration.setHtmlTitle(LocalizedString.fromJson(object.get("htmlTitle")));
        }
        if (object.has("applicationSubTitle")) {
            configuration.setApplicationSubTitle(LocalizedString.fromJson(object.get("applicationSubTitle")));
        }
        if (object.has("applicationCopyright")) {
            configuration.setApplicationCopyright(LocalizedString.fromJson(object.get("applicationCopyright")));
        }
        if (object.has("supportEmailAddress")) {
            configuration.setSupportEmailAddress(object.get("supportEmailAddress").getAsString());
        }
        if (object.has("systemEmailAddress")) {
            configuration.setSystemEmailAddress(object.get("systemEmailAddress").getAsString());
        }
        if (object.has("theme")) {
            configuration.setTheme(object.get("theme").getAsString());
        }
        if (object.has("logo")) {
            configuration.setLogo(Base64.decodeBase64(object.get("logo").getAsString()));
        }
        if (object.has("logoType")) {
            configuration.setLogoType(object.get("logoType").getAsString());
        }
        if (object.has("menu")) {
            final JsonObject menuObj = object.get("menu").getAsJsonObject();
            if (menuObj.has("id")) {
                final String menuExternalId = menuObj.get("id").getAsString();
                MenuItem menu = FenixFramework.getDomainObject(menuExternalId);
                if (!menu.equals(configuration.getMenu())) {
                    configuration.setMenu(menu);
                }
            }
        }
        return configuration;
    }
}
