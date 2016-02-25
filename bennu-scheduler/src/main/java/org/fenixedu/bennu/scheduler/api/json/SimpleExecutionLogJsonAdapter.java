package org.fenixedu.bennu.scheduler.api.json;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SimpleExecutionLogJsonAdapter implements JsonViewer<ExecutionLog> {

    @Override
    public JsonElement view(ExecutionLog obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getId());
        json.addProperty("start", obj.getStart().toString());
        obj.getEnd().ifPresent(val -> json.addProperty("end", val.toString()));
        json.addProperty("state", obj.getState().name());
        json.addProperty("taskName", obj.getTaskName());
        return json;
    }
}
