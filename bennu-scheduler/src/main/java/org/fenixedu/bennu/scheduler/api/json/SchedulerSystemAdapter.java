package org.fenixedu.bennu.scheduler.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.api.json.DomainObjectViewer;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.io.domain.FileSupport;
import org.fenixedu.bennu.io.domain.LocalFileSystemStorage;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(SchedulerSystem.class)
public class SchedulerSystemAdapter implements JsonViewer<SchedulerSystem> {

    @Override
    public JsonElement view(SchedulerSystem obj, JsonBuilder ctx) {
        final JsonObject json = new JsonObject();
        json.addProperty("running", SchedulerSystem.isRunning());
        json.add("loggingStorage", ctx.view(obj.getLoggingStorage(), LocalFileSystemStorage.class, DomainObjectViewer.class));
        json.add(
                "availableStorages",
                ctx.view(FileSupport.getInstance().getFileStorageSet().stream()
                        .filter(store -> store instanceof LocalFileSystemStorage)));
        return json;
    }
}
