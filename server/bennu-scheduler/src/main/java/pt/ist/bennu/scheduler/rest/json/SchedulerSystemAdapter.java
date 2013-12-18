package pt.ist.bennu.scheduler.rest.json;

import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.rest.json.DomainObjectViewer;
import pt.ist.bennu.io.domain.FileSupport;
import pt.ist.bennu.io.domain.LocalFileSystemStorage;
import pt.ist.bennu.scheduler.domain.SchedulerSystem;

import com.google.common.collect.FluentIterable;
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
                ctx.view(FluentIterable.from(FileSupport.getInstance().getFileStoragesSet()).filter(LocalFileSystemStorage.class)
                        .toSet()));
        return json;
    }
}
