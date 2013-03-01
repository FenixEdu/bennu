package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(BennuGroup.class)
public class GroupJsonAdapter implements JsonAdapter<BennuGroup> {
    @Override
    public BennuGroup create(JsonElement json, JsonBuilder ctx) {
        return BennuGroup.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public BennuGroup update(JsonElement json, BennuGroup obj, JsonBuilder ctx) {
        return BennuGroup.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public JsonElement view(BennuGroup group, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("expression", group.expression());
        return object;
    }
}
