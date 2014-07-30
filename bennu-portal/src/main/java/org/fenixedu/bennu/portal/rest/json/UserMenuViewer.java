package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(MenuItem.class)
public class UserMenuViewer implements JsonViewer<MenuItem> {

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("path", obj.getPath());
        json.addProperty("fullPath", obj.getFullPath());
        json.add("description", ctx.view(obj.getDescription()));
        json.add("title", ctx.view(obj.getTitle()));

        if (obj.isMenuContainer()) {
            MenuContainer container = obj.getAsMenuContainer();
            if (container.isRoot()) {
                json.add("title", ctx.view(PortalConfiguration.getInstance().getApplicationTitle()));
            }
            json.add("menu", ctx.view(container.getUserMenuStream()));
        }
        return json;
    }
}
