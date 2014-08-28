package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonCreator;
import org.fenixedu.bennu.core.json.JsonUpdater;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MenuItemAdapter implements JsonViewer<MenuItem>, JsonUpdater<MenuItem>, JsonCreator<MenuContainer> {

    @Override
    public MenuItem update(JsonElement json, MenuItem item, JsonBuilder ctx) {
        JsonObject jsonObj = json.getAsJsonObject();
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
        if (jsonObj.has("visible")) {
            item.setVisible(jsonObj.get("visible").getAsBoolean());
        }
        if (jsonObj.has("accessExpression")) {
            item.setAccessGroup(Group.parse(jsonObj.get("accessExpression").getAsString()));
        }
        return item;
    }

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("order", obj.getOrd());
        json.addProperty("path", obj.getPath());
        json.addProperty("fullPath", obj.getFullPath());
        json.addProperty("accessExpression", obj.getAccessGroup().getExpression());
        json.addProperty("functionality", obj.isMenuFunctionality());
        json.addProperty("visible", obj.isVisible());
        json.addProperty("layout", obj.getLayout());
        json.add("description", ctx.view(obj.getDescription()));
        json.add("title", ctx.view(obj.getTitle()));

        if (obj.isMenuContainer()) {
            MenuContainer container = obj.getAsMenuContainer();
            if (container.isRoot()) {
                json.add("title", ctx.view(PortalConfiguration.getInstance().getApplicationTitle()));
            }
            json.add("menu", ctx.view(container.getOrderedChild(), MenuItemAdapter.class));
        } else {
            MenuFunctionality functionality = obj.getAsMenuFunctionality();
            json.addProperty("key", functionality.getItemKey());
            json.addProperty("provider", functionality.getProvider());
        }
        return json;
    }

    @Override
    public MenuContainer create(JsonElement json, JsonBuilder ctx) {
        JsonObject jsonObj = json.getAsJsonObject();
        MenuContainer parent = FenixFramework.getDomainObject(jsonObj.get("parent").getAsString());
        boolean visible = jsonObj.get("visible").getAsBoolean();
        String accessGroup = jsonObj.get("accessExpression").getAsString();
        LocalizedString description = LocalizedString.fromJson(jsonObj.get("description"));
        LocalizedString title = LocalizedString.fromJson(jsonObj.get("title"));
        String path = jsonObj.get("path").getAsString();
        MenuContainer container = new MenuContainer(parent, visible, accessGroup, description, title, path);
        if (jsonObj.has("layout")) {
            container.setLayout(jsonObj.get("layout").getAsString());
        }
        return container;
    }
}
