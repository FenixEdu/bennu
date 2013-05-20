package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;
import pt.ist.fenixframework.core.AbstractDomainObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(AbstractDomainObject.class)
public class DomainObjectViewer implements JsonViewer<AbstractDomainObject> {

    @Override
    public JsonElement view(AbstractDomainObject obj, JsonBuilder ctx) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", obj == null ? "null" : obj.getExternalId());
        return jsonObject;
    }

}
