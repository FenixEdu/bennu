package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(PersistentGroup.class)
public class GroupJsonAdapter implements JsonAdapter<PersistentGroup> {
    @Override
    public PersistentGroup create(JsonElement json, JsonBuilder ctx) {
        return PersistentGroup.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public PersistentGroup update(JsonElement json, PersistentGroup obj, JsonBuilder ctx) {
        return PersistentGroup.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public JsonElement view(PersistentGroup group, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("expression", group.expression());
        return object;
    }
}
