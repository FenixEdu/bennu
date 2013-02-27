package pt.ist.bennu.portal.rest;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.portal.domain.MenuItem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(MenuItem.class)
public class MenuItemAdapter implements JsonAdapter<MenuItem> {

    @Override
    public MenuItem update(JsonElement json, MenuItem obj, JsonBuilder ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("path", obj.getPath());
        json.addProperty("title", obj.getTitle().getContent());
        json.addProperty("description", obj.getDescription().getContent());
        json.add("menu", ctx.view(obj.getChild()));
        return json;
    }

    @Override
    public MenuItem create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

}
