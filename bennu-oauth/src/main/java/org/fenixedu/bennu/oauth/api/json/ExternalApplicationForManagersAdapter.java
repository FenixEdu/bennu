package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ExternalApplicationForManagersAdapter extends ExternalApplicationAdapter {

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication obj, JsonBuilder ctx) {

        ExternalApplication updated = super.update(json, obj, ctx);
        JsonObject jObj = json.getAsJsonObject();
        updated.setAuthorName(jObj.get("author") == null ? "" : jObj.get("author").getAsString());
        return updated;

    }

}
