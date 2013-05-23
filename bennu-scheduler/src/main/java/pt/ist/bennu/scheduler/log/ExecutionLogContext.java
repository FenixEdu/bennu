package pt.ist.bennu.scheduler.log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.domain.SchedulerSystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class ExecutionLogContext {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionLogContext.class);

    private ConcurrentHashMap<String, JsonObject> logsMap = new ConcurrentHashMap<>();
    private Long lastModified;

    public ExecutionLogContext() {
    }

    private synchronized boolean hasChanged(File file) {
        final long modified = file.lastModified();
        if (lastModified == null) {
            lastModified = modified;
            return true;
        }
        final boolean result = modified > lastModified;
        LOG.info("File has changed ? : {}", result);
        if (result) {
            lastModified = modified;
            return true;
        }
        return false;
    }

    private synchronized void updateLogMap() {
        final File file = new File(getLogFilePath());
        if (file.exists() && hasChanged(file)) {
            try (FileReader fileReader = new FileReader(file)) {
                JsonStreamParser parser = new JsonStreamParser(fileReader);
                while (parser.hasNext()) {
                    final JsonObject jsonLog = parser.next().getAsJsonObject();
                    final String jsonLogId = jsonLog.get("id").getAsString();
                    final JsonObject jsonLogFromMap = logsMap.get(jsonLogId);
                    if (jsonLogFromMap != null) {
                        update(jsonLog, jsonLogFromMap);
                    } else {
                        logsMap.put(jsonLogId, jsonLog);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getLogFilePath() {
        return SchedulerSystem.getLogsPath().concat(getLogFileName());
    }

    protected String getLogFileName() {
        return ExecutionLog.LOG_JSON_FILENAME;
    }

    private static void update(JsonObject source, JsonObject target) {
        if (source.equals(target)) {
            return;
        }
        for (final Entry<String, JsonElement> entry : source.entrySet()) {
            target.add(entry.getKey(), entry.getValue());
        }
    }

    public JsonObject get(String key) {
        updateLogMap();
        return logsMap.get(key);
    }

    public Collection<JsonObject> values() {
        updateLogMap();
        return logsMap.values();
    }

    public Boolean isEmpty() {
        updateLogMap();
        return logsMap.isEmpty();
    }
}
