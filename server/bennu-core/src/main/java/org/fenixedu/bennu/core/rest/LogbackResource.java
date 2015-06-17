package org.fenixedu.bennu.core.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.groups.Group;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/bennu-core/system/logger")
public class LogbackResource extends BennuRestResource {

    private static boolean available = checkIfIsAvailable();
    private static String serverName;

    static {
        try {
            serverName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            serverName = "UNKNOWN";
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllLoggers() {
        accessControl(Group.managers());
        if (available) {
            return Holder.getAllLoggers().toString();
        } else {
            return "{ 'loggers': [] }";
        }
    }

    @POST
    @Path("/{name}/{level}")
    public String setLogLevel(@PathParam("name") String loggerName, @PathParam("level") String level) {
        accessControl(Group.managers());
        if (available) {
            Holder.setLevel(loggerName, level);
        }
        return getAllLoggers();
    }

    /*
     * Holder class that will interact with logback.
     * 
     * The purpose of this class is to avoid class loading errors if logback is not available
     */
    private static final class Holder {
        public static JsonObject getAllLoggers() {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JsonArray array = new JsonArray();
            for (Logger logger : context.getLoggerList()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", logger.getName());
                obj.addProperty("level", logger.getEffectiveLevel().toString());
                array.add(obj);
            }
            JsonObject obj = new JsonObject();
            obj.add("loggers", array);
            obj.addProperty("server", serverName);
            return obj;
        }

        public static void setLevel(String loggerName, String level) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.getLogger(loggerName).setLevel(Level.valueOf(level));
        }
    }

    /*
     * Checks if Logback is in the classpath
     */
    private static boolean checkIfIsAvailable() {
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
