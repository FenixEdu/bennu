package org.fenixedu.bennu.core.json.adapters;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;

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
