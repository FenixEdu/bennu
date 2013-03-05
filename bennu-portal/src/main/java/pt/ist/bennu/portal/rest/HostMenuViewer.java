package pt.ist.bennu.portal.rest;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(VirtualHost.class)
public class HostMenuViewer implements JsonViewer<VirtualHost> {
    @Override
    public JsonElement view(VirtualHost obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.add("title", ctx.view(obj.getInfo().getApplicationTitle()));
        json.add("menu", ctx.view(obj.getMenu().getChild()));
        return json;
    }
}
