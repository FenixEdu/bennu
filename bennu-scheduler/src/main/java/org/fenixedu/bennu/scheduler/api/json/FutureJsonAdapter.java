package org.fenixedu.bennu.scheduler.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.scheduler.future.PersistentFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(PersistentFuture.class)
public class FutureJsonAdapter implements JsonViewer<PersistentFuture> {

    @Override
    public JsonElement view(PersistentFuture obj, JsonBuilder ctx) {
        final JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("desc", obj.getShortDescription());
        json.addProperty("created", obj.getCreated().toString());
        if (obj.getStartedExecution() != null) {
            json.addProperty("start", obj.getStartedExecution().toString());
        }
        if (obj.getFinishedExecution() != null) {
            json.addProperty("end", obj.getFinishedExecution().toString());
        }
        json.addProperty("state", obj.getState().toString());
        return json;
    }
}
