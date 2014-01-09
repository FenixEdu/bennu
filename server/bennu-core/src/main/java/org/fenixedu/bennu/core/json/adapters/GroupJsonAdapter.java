package org.fenixedu.bennu.core.json.adapters;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.groups.Group;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Group.class)
public class GroupJsonAdapter implements JsonAdapter<Group> {
    @Override
    public Group create(JsonElement json, JsonBuilder ctx) {
        return Group.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public Group update(JsonElement json, Group obj, JsonBuilder ctx) {
        return Group.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public JsonElement view(Group group, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("expression", group.expression());
        object.addProperty("name", group.getPresentationName());
        object.addProperty("accessible", group.isMember(Authenticate.getUser()));
        return object;
    }
}
