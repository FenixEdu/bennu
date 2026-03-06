package org.fenixedu.bennu.portal.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;

public class EntrypointViewer implements JsonViewer<MenuFunctionality> {

    @Override
    public JsonElement view(MenuFunctionality obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("path", obj.getPath());
        json.addProperty("fullPath", obj.getFullPath());
        json.add("title", ctx.view(obj.getTitle()));
        json.add("description", ctx.view(obj.getDescription()));
        json.addProperty("icon", obj.getIcon());

        return json;
    }

}
