package pt.ist.bennu.portal.rest;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.util.MultiLanguageString;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(MenuItem.class)
public class MenuItemAdapter implements JsonAdapter<MenuItem> {
    /*
        {   "order" : 1, 
            "path": "/xpto", 
            "title" : { "pt" : "xpto" }, 
            "description" : { "pt" : "this is a xpto functionality" }, 
            "menu" : [
                        {
                          "order" : 1,
                          "path" : "/xpto/1",
                          "title" : { "pt" : "xpto sub 1" },
                          "description" : { "pt" : "this is a xpto sub 1 functionality" }
                        },
                        {
                          "order" : 2,
                          "path" : "/xpto/2",
                          "title" : { "pt" : "xpto sub 2" },
                          "description" : { "pt" : "this is a xpto sub 2 functionality" }
                        }
                    ]
        }
     */

    @Override
    public MenuItem update(JsonElement json, MenuItem menuItem, JsonBuilder ctx) {
        JsonObject jsonObj = json.getAsJsonObject();
        setMenuInfo(jsonObj, menuItem);

        if (jsonObj.has("menu")) {
            final JsonArray menu = jsonObj.get("menu").getAsJsonArray();
            for (JsonElement menuEl : menu) {
                menuItem.addChild(update(menuEl, getMenuItem(menuEl), ctx));
            }
        }

        return menuItem;
    }

    private MenuItem getMenuItem(JsonElement menuJson) {
        return AbstractDomainObject.fromExternalId(menuJson.getAsJsonObject().get("id").getAsString());
    }

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("order", obj.getOrd());
        json.addProperty("path", obj.getPath());
        json.addProperty("title", emptyIfNull(obj.getTitle()));
        json.addProperty("description", emptyIfNull(obj.getDescription()));
        json.add("menu", ctx.view(obj.getOrderedChild()));
        return json;
    }

    private String emptyIfNull(MultiLanguageString obj) {
        return (obj == null || obj.isEmpty()) ? StringUtils.EMPTY : obj.getContent();
    }

    @Override
    public MenuItem create(JsonElement json, JsonBuilder ctx) {
        JsonObject jsonObj = json.getAsJsonObject();
        MenuItem menuItem = new MenuItem();
        setMenuInfo(jsonObj, menuItem);

        if (jsonObj.has("menu")) {
            final JsonArray menu = jsonObj.get("menu").getAsJsonArray();
            for (JsonElement menuEl : menu) {
                menuItem.addChild(create(menuEl, ctx));
            }
        }
        return menuItem;
    }

    public void setMenuInfo(JsonObject jsonObj, MenuItem menuItem) {
        if (jsonObj.has("title")) {
            menuItem.setTitle(MultiLanguageString.fromJson(jsonObj.get("title").getAsJsonObject()));
        }
        if (jsonObj.has("path")) {
            menuItem.setPath(jsonObj.get("path").getAsString());
        }
        if (jsonObj.has("description")) {
            menuItem.setDescription(MultiLanguageString.fromJson(jsonObj.get("description").getAsJsonObject()));
        }
        if (jsonObj.has("order")) {
            menuItem.setOrd(jsonObj.get("order").getAsInt());
        }
    }

}
