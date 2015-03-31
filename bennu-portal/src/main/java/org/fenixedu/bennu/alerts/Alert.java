package org.fenixedu.bennu.alerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * An Alert is a message intended to be presented to the user. This class uses the {@link org.fenixedu.bennu.alerts.FlashMap} to
 * retain alerts on POST/Redirect/GET scenarios,
 * ensuring a right delivery to the user. Users should only use the static methods to emit new messages to users.
 *
 * @author Artur Ventura (artur.ventura@tecnico.pt)
 * @since 3.5.0
 */
public class Alert {
    private static final Logger LOGGER = LoggerFactory.getLogger(Alert.class);
    private static final String ATTRIB_NAME = "__bennu-alerts__";
    private String message;
    private AlertType type;

    private Alert(String message, AlertType type) {
        this.setMessage(message);
        this.setType(type);
    }

    private static void alert(HttpServletRequest request, String message, AlertType type) {
        RedirectAttributes.initFromRequest(request);
        Map<String, Object> attr = RedirectAttributes.getOutputFlashMap(request);
        Object o = attr.get(ATTRIB_NAME);

        if (o == null) {
            o = new ArrayList<Alert>();
            attr.put(ATTRIB_NAME, o);
        }

        if (o instanceof ArrayList<?>) {
            ((ArrayList<Alert>) o).add(new Alert(message, type));
        } else {
            LOGGER.error("Can not add alert to list, object in attribute is not a ArrayList");
        }
    }

    /**
     * Issues a success message to the user.
     *
     * @param request the request that originated the alert
     * @param message the message you wish to present to the user
     */
    public static void success(HttpServletRequest request, String message) {
        alert(request, message, AlertType.SUCCESS);
    }

    /**
     * Issues a info message to the user.
     *
     * @param request the request that originated the alert
     * @param message the message you wish to present to the user
     */
    public static void info(HttpServletRequest request, String message) {
        alert(request, message, AlertType.INFO);
    }

    /**
     * Issues a danger message to the user.
     *
     * @param request the request that originated the alert
     * @param message the message you wish to present to the user
     */
    public static void danger(HttpServletRequest request, String message) {
        alert(request, message, AlertType.DANGER);
    }

    /**
     * Issues a warning message to the user.
     *
     * @param request the request that originated the alert
     * @param message the message you wish to present to the user
     */
    public static void warning(HttpServletRequest request, String message) {
        alert(request, message, AlertType.WARNING);
    }

    /**
     * This method will try to look up all avaible alerts to be shown.
     * All alerts processed here are removed and not show again.
     * This method must be called before flushing the flash maps into the session,
     * since some are may be consumed in the current request if the response is not a responde
     *
     * @param request the current request
     * @param response the current response
     * @return a list of alerts to process
     */
    public static List<Alert> getAlerts(HttpServletRequest request, HttpServletResponse response) {
        ArrayList<Alert> alerts = (ArrayList<Alert>) RedirectAttributes.getInputFlashMap(request).get(ATTRIB_NAME);

        if (alerts == null) {
            alerts = new ArrayList<Alert>();
        }

        if (response.getStatus() == HttpServletResponse.SC_OK) {
            FlashMap map = RedirectAttributes.getOutputFlashMap(request);
            if (map != null) {
                ArrayList<Alert> newAlerts = (ArrayList<Alert>) map.remove(ATTRIB_NAME);
                if (newAlerts != null) {
                    alerts.addAll(newAlerts);
                }
            }
        }

        return alerts;
    }

    /**
     * This method will try to look up all avaible alerts to be shown.
     * All alerts processed here are removed and not show again.
     * This method must be called before flushing the flash maps into the session,
     * since some are may be consumed in the current request if the response is not a responde.
     * This is a helper method to get all alerts in a JsonFormat
     *
     * @param request the current request
     * @param response the current response
     * @return a JsonArray of alerts to process
     */
    public static JsonArray getAlertsAsJson(HttpServletRequest request, HttpServletResponse response) {
        JsonArray array = new JsonArray();
        List<Alert> alerts = getAlerts(request, response);

        for (Alert alert : alerts) {
            JsonObject o = new JsonObject();

            o.addProperty("message", alert.getMessage());
            o.addProperty("type", alert.getTag());
            array.add(o);
        }
        RedirectAttributes.flush(request, response);
        return array;
    }

    /**
     * Returns the alert message
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;

    }

    /**
     * Returns the type of alert
     *
     * @return the {@link AlertType} associated with this message
     */

    public AlertType getType() {
        return type;
    }

    private void setType(AlertType type) {
        this.type = type;
    }

    /**
     * Returns the tag associated with this {@link AlertType}
     *
     * @return the tag
     */
    public String getTag() {
        return getType().getTag();
    }

}
