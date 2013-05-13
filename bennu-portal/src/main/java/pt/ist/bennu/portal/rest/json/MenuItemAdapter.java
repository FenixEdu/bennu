package pt.ist.bennu.portal.rest.json;

import java.util.Locale;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.i18n.InternationalString;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.dispatch.AppServer;
import pt.ist.bennu.dispatch.model.FunctionalityInfo;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.fenixframework.FenixFramework;

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
                final MenuItem childMenuItem = getMenuItem(menuEl);
                childMenuItem.setParent(menuItem);
                update(menuEl, childMenuItem, ctx);
            }
        }

        return menuItem;
    }

    private MenuItem getMenuItem(JsonElement menuJson) {
        return FenixFramework.getDomainObject(menuJson.getAsJsonObject().get("id").getAsString());
    }

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("order", obj.getOrd());
        json.addProperty("path", obj.getPath());
        json.addProperty("functionality", obj.isFunctionalityLink());
        json.addProperty("accessExpression", obj.getAccessExpression());
        if (obj.getHost() != null) {
            InternationalString mls = new InternationalString();
            for (Locale locale : ConfigurationManager.getSupportedLocales()) {
                mls = mls.with(locale, obj.getHost().getHostname());
            }
            json.add("title", ctx.view(mls));
        } else {
            json.add("title", ctx.view(obj.getTitle()));
        }
        json.add("description", ctx.view(obj.getDescription()));
        json.add("menu", ctx.view(obj.getOrderedChild()));
        return json;
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
            menuItem.setTitle(InternationalString.fromJson(jsonObj.get("title").getAsJsonObject()));
        }
        if (jsonObj.has("path")) {
            final String path = jsonObj.get("path").getAsString();
            final FunctionalityInfo functionalityInfo = AppServer.getFunctionalityInfo(path);
            if (functionalityInfo != null) {
                menuItem.setAccessExpression(functionalityInfo.getGroup());
            }
            menuItem.setPath(path);
        }
        if (jsonObj.has("description")) {
            menuItem.setDescription(InternationalString.fromJson(jsonObj.get("description").getAsJsonObject()));
        }
        if (jsonObj.has("order")) {
            menuItem.setOrd(jsonObj.get("order").getAsInt());
        }
    }

}
