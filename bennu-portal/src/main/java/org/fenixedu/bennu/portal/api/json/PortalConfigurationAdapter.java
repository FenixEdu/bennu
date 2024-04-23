package org.fenixedu.bennu.portal.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.DomainObjectViewer;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonUpdater;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalInitializer;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@DefaultJsonAdapter(PortalConfiguration.class)
public class PortalConfigurationAdapter implements JsonViewer<PortalConfiguration>, JsonUpdater<PortalConfiguration> {

    private static final boolean developmentMode = CoreConfiguration.getConfiguration().developmentMode();

    @Override
    public JsonElement view(PortalConfiguration configuration, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("id", configuration.getExternalId());
        object.add("applicationTitle", configuration.getApplicationTitle().json());
        object.add("htmlTitle", configuration.getHtmlTitle().json());
        object.add("applicationSubTitle", configuration.getApplicationSubTitle().json());
        object.add("applicationCopyright", configuration.getApplicationCopyright().json());
        object.addProperty("developmentMode", developmentMode);
        object.addProperty("supportEmailAddress", configuration.getSupportEmailAddress());
        object.addProperty("systemEmailAddress", configuration.getSystemEmailAddress());
        object.addProperty("theme", configuration.getTheme());
        object.addProperty("defaultDocumentationBaseUrl", configuration.getDocumentationBaseUrl());
        object.addProperty("loginPath", configuration.getLoginPath());
        object.addProperty("recoveryLinkPath", configuration.getRecoveryLinkPath());
        object.addProperty("signUpPath", configuration.getSignUpPath());
        object.addProperty("detectBrowserLocalInLoginPage", configuration.getDetectBrowserLocalInLoginPage());
        object.addProperty("supportUrl", configuration.getSupportUrl());

        if (configuration.getLogo() != null) {
            if (!Strings.isNullOrEmpty(configuration.getLogoLinkUrl())) {
                object.addProperty("logoLinkUrl", configuration.getLogoLinkUrl());
            }
            if (!Strings.isNullOrEmpty(configuration.getLogoTooltip())) {
                object.addProperty("logoTooltip", configuration.getLogoTooltip());
            }
        }
        JsonArray themes = new JsonArray();
        for (String theme : PortalInitializer.getThemes()) {
            themes.add(new JsonPrimitive(theme));
        }
        object.add("themes", themes);
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
        if (object.has("logoLinkUrl")) {
            configuration.setLogoLinkUrl(object.get("logoLinkUrl").getAsString());
        }
        if (object.has("logoTooltip")) {
            configuration.setLogoTooltip(object.get("logoTooltip").getAsString());
        }
        if (object.has("favicon")) {
            configuration.setFavicon(BaseEncoding.base64().decode(object.get("favicon").getAsString()));
        }
        if (object.has("faviconType")) {
            configuration.setFaviconType(object.get("faviconType").getAsString());
        }
        if (object.has("defaultDocumentationBaseUrl")) {
            configuration.setDocumentationBaseUrl(object.get("defaultDocumentationBaseUrl").getAsString());
        }
        if (object.has("loginPath")) {
            configuration.setLoginPath(object.get("loginPath").getAsString());
        }
        if (object.has("recoveryLinkPath")) {
            configuration.setRecoveryLinkPath(object.get("recoveryLinkPath").getAsString());
        }
        if (object.has("signUpPath")) {
            configuration.setSignUpPath(object.get("signUpPath").getAsString());
        }
        if (object.has("detectBrowserLocalInLoginPage")) {
            configuration.setDetectBrowserLocalInLoginPage(object.get("detectBrowserLocalInLoginPage").getAsBoolean());
        }
        if (object.has("supportUrl")) {
            configuration.setSupportUrl(object.get("supportUrl").getAsString());
        }

        return configuration;
    }
}
