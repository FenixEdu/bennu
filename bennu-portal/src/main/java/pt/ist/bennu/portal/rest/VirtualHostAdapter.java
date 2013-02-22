package pt.ist.bennu.portal.rest;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class VirtualHostAdapter implements JsonAdapter<VirtualHost> {

    @Override
    public VirtualHost create(JsonElement json, JsonBuilder ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VirtualHost update(JsonElement json, VirtualHost obj, JsonBuilder ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonElement view(VirtualHost obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("title", obj.getInfo().getApplicationTitle().getContent());
        json.add("menu", ctx.view(obj.getMenu().getChild()));
        return json;
    }

}
