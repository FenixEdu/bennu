package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.rest.json.DomainObjectViewer;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonUpdater;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.core.util.Base64;

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
            object.addProperty("logo", BaseEncoding.base64().encode(configuration.getLogo()));
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
            configuration.setLogo(BaseEncoding.base64().decode(object.get("logo").getAsString()));
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
