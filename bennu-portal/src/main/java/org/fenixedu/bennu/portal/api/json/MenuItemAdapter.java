package org.fenixedu.bennu.portal.api.json;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonCreator;
import org.fenixedu.bennu.core.json.JsonUpdater;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.domain.SupportConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.FenixFramework;

public class MenuItemAdapter implements JsonViewer<MenuItem>, JsonUpdater<MenuItem>, JsonCreator<MenuContainer> {

    @Override
    public MenuItem update(JsonElement json, MenuItem item, JsonBuilder ctx) {
        final JsonObject jsonObj = json.getAsJsonObject();
        if (jsonObj.has("title")) {
            item.setTitle(LocalizedString.fromJson(jsonObj.get("title").getAsJsonObject()));
        }
        if (jsonObj.has("description")) {
            item.setDescription(LocalizedString.fromJson(jsonObj.get("description").getAsJsonObject()));
        }
        if (jsonObj.has("layout")) {
            item.setLayout(jsonObj.get("layout").getAsString());
        } else {
            item.setLayout(null);
        }
        if (jsonObj.has("icon")) {
            item.setIcon(jsonObj.get("icon").getAsString());
        } else {
            item.setIcon(null);
        }
        if (jsonObj.has("visible")) {
            item.setVisible(jsonObj.get("visible").getAsBoolean());
        }
        if (jsonObj.has("accessExpression")) {
            item.setAccessGroup(Group.parse(jsonObj.get("accessExpression").getAsString()));
        }
        if (item.isMenuFunctionality() && jsonObj.has("documentationUrl")) {
            item.getAsMenuFunctionality().setDocumentationUrl(jsonObj.get("documentationUrl").getAsString());
        }

        if (jsonObj.has("supportConfig")) {
            final SupportConfiguration supportConfiguration =
                    FenixFramework.getDomainObject(jsonObj.get("supportConfig").getAsString());
            if (supportConfiguration != null) {
                item.setSupport(supportConfiguration);
            }
        }
        if (jsonObj.has("faqUrl")) {
            item.setFaqUrl(jsonObj.get("faqUrl").getAsString());
        } else {
            item.setFaqUrl(null);
        }
        return item;
    }

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        final JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("order", obj.getOrd());
        json.addProperty("path", obj.getPath());
        json.addProperty("fullPath", obj.getFullPath());
        json.addProperty("accessExpression", obj.getAccessGroup().getExpression());
        json.addProperty("functionality", obj.isMenuFunctionality());
        json.addProperty("visible", obj.isVisible());
        json.addProperty("layout", obj.getLayout());
        json.addProperty("icon", obj.getIcon());
        json.add("description", ctx.view(obj.getDescription()));
        json.add("title", ctx.view(obj.getTitle()));
        json.add("supportConfigs", ctx.view(Bennu.getInstance().getSupportConfigurationSet(), SupportConfigurationAdapter.class));
        json.add("supportConfig", ctx.view(obj.getSupport(), SupportConfigurationAdapter.class));
        json.addProperty("faqUrl", obj.getFaqUrl());
        if (obj.isMenuContainer()) {
            final MenuContainer container = obj.getAsMenuContainer();
            if (container.isRoot()) {
                json.add("title", ctx.view(PortalConfiguration.getInstance().getApplicationTitle()));
                json.addProperty("appRoot", true);
            }
            json.add("menu", ctx.view(container.getOrderedChild(), MenuItemAdapter.class));
            json.addProperty("subRoot", container.isSubRoot());
        } else {
            final MenuFunctionality functionality = obj.getAsMenuFunctionality();
            json.addProperty("key", functionality.getItemKey());
            json.addProperty("provider", functionality.getProvider());
            json.addProperty("documentationUrl", functionality.getDocumentationUrl());
            json.addProperty("documentationUrlProcessed", functionality.getParsedDocumentationUrl());

        }

        return json;
    }

    @Override
    public MenuContainer create(JsonElement json, JsonBuilder ctx) {
        final JsonObject jsonObj = json.getAsJsonObject();
        final MenuContainer parent = FenixFramework.getDomainObject(jsonObj.get("parent").getAsString());
        final boolean visible = jsonObj.get("visible").getAsBoolean();
        final String accessGroup = jsonObj.get("accessExpression").getAsString();
        final LocalizedString description = LocalizedString.fromJson(jsonObj.get("description"));
        final LocalizedString title = LocalizedString.fromJson(jsonObj.get("title"));
        final String path = jsonObj.get("path").getAsString();
        final MenuContainer container = new MenuContainer(parent, visible, accessGroup, description, title, path);
        if (jsonObj.has("layout")) {
            container.setLayout(jsonObj.get("layout").getAsString());
        }
        if (jsonObj.has("faqUrl")) {
            container.setLayout(jsonObj.get("faqUrl").getAsString());
        }
        return container;
    }
}
