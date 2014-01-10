package org.fenixedu.bennu.scheduler.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ExecutionLog {

    private static Gson gson;
    private String id;
    private DateTime start;
    private DateTime end;
    private boolean success;
    private String taskName;
    private String stackTrace;
    private Set<String> files;
    private Boolean started = false;

    public static final String LOG_JSON_FILENAME = "executionLog.json";

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {

            @Override
            public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        });
        gsonBuilder.registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {

            @Override
            public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return new DateTime(json.getAsString());
            }
        });
        gsonBuilder.setPrettyPrinting();
//        gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
//
//            @Override
//            public boolean shouldSkipField(FieldAttributes f) {
//                return f.getDeclaredType().equals(Throwable.class);
//            }
//
//            @Override
//            public boolean shouldSkipClass(Class<?> clazz) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });

        gson = gsonBuilder.create();
    }

    public static Gson getGson() {
        return gson;
    }

    public ExecutionLog(String taskName) {
        setId(new DateTime().toString("MMddyyyy-kkmmss"));
        setSuccess(false);
        setStart(new DateTime());
        setTaskName(taskName);
    }

    public ExecutionLog(JsonObject obj) {
        setId(obj.get("id").getAsString());
        setSuccess(obj.get("success").getAsBoolean());
        setStart(new DateTime(obj.get("start").getAsString()));
        setEnd(new DateTime(obj.get("end").getAsString()));
        setTaskName(obj.get("taskName").getAsString());
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private void writeJson(String json) {
        if (Strings.isNullOrEmpty(json)) {
            return;
        }
        synchronized (getLock()) {
            try (FileOutputStream fos = getLogFileOutputStream()) {
                fos.write(json.getBytes());
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileOutputStream getLogFileOutputStream() throws IOException {
        File file = new File(getLogFilePath());
        if (!file.exists()) {
            file.createNewFile();
        }
        return new FileOutputStream(file, true);
    }

    protected String getLogFilePath() {
        return SchedulerSystem.getLogsPath().concat(LOG_JSON_FILENAME);
    }

    public void persist() {
        writeJson(gson.toJson(this));
    }

    public void setError(Throwable t) {
        StringWriter errors = new StringWriter();
        final PrintWriter printErrors = new PrintWriter(errors);
        t.printStackTrace(printErrors);
        printErrors.flush();
        stackTrace = errors.toString();
    }

    public String getError() {
        return stackTrace;
    }

    public void addFile(String filename) {
        if (files == null) {
            files = new HashSet<>();
        }
        files.add(filename);
    }

    protected Object getLock() {
        return LOG_JSON_FILENAME;
    }

    public void start() {
        if (!started) {
            setStart(new DateTime());
            persist();
            started = true;
        }
    }

    public void end() {
        setEnd(new DateTime());
        persist();
    }

}
