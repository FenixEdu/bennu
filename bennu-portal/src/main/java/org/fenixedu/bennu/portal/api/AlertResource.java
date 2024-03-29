package org.fenixedu.bennu.portal.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.PersistentAlertMessage;
import pt.ist.fenixframework.Atomic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/bennu-portal/alert")
public class AlertResource extends BennuRestResource {

    @POST
    @Path("/count")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Atomic(mode = Atomic.TxMode.WRITE)
    public JsonElement count(final JsonObject json) {
        final User user = Authenticate.getUser();
        if (user != null && json != null) {
            final String message = JsonUtils.get(json, "message");
            final String type = JsonUtils.get(json, "type");
            user.getPersistentAlertMessageUserViewCountSet().stream()
                    .filter(alert -> alert.getPersistentAlertMessage().getHideAfterViewCount() != null)
                    .filter(alert -> alert.getPersistentAlertMessage().getType().getTag().equals(type))
                    .filter(alert -> alert.getPersistentAlertMessage().getMessage().anyMatch(s ->
                            s.replace("\\", "").equals(message.replace("\\", ""))))
                    .forEach(alert -> {
                        final int count = alert.getViewCount() + 1;
                        final PersistentAlertMessage persistentAlertMessage = alert.getPersistentAlertMessage();
                        if (count >= persistentAlertMessage.getHideAfterViewCount().intValue()) {
                            alert.delete();
                            if (persistentAlertMessage.getPersistentAlertMessageUserViewCountSet().isEmpty()) {
                                persistentAlertMessage.delete();
                            }
                        } else {
                            alert.setViewCount(count);
                        }
                    });
        }
        return JsonUtils.toJson(data -> data.addProperty("status", "ok"));
    }

    @POST
    @Path("/dismiss")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Atomic(mode = Atomic.TxMode.WRITE)
    public JsonElement dismiss(final JsonObject json) {
        final User user = Authenticate.getUser();
        if (user != null && json != null) {
            final String message = JsonUtils.get(json, "message");
            final String type = JsonUtils.get(json, "type");
            user.getPersistentAlertMessageUserViewCountSet().stream()
                    .filter(alert -> alert.getPersistentAlertMessage().getType().getTag().equals(type))
                    .filter(alert -> alert.getPersistentAlertMessage().getMessage().anyMatch(s ->
                            s.replace("\\", "").equals(message.replace("\\", ""))))
                    .forEach(alert -> {
                        final PersistentAlertMessage persistentAlertMessage = alert.getPersistentAlertMessage();
                        alert.delete();
                        if (persistentAlertMessage.getPersistentAlertMessageUserViewCountSet().isEmpty()) {
                            persistentAlertMessage.delete();
                        }
                    });
        }
        return JsonUtils.toJson(data -> data.addProperty("status", "ok"));
    }

}
