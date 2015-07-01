package org.fenixedu.bennu.scheduler.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(ExecutionLog.class)
public class ExecutionLogJsonAdapter implements JsonViewer<ExecutionLog> {

    @Override
    public JsonElement view(ExecutionLog obj, JsonBuilder ctx) {
        return obj.json();
    }

}
