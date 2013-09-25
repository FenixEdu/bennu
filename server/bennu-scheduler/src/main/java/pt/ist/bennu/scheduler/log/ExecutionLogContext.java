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

    private ConcurrentHashMap<String, JsonObject> logsMap;
    private Long lastModified;

    public ExecutionLogContext() {
        logsMap = new ConcurrentHashMap<>();
        lastModified = Long.MIN_VALUE;
    }

    private boolean hasChanged(File file) {
        final long modified = file.lastModified();
        if (modified > lastModified) {
            LOG.debug("Execution log was modified : before {} now {}", lastModified, modified);
            lastModified = modified;
            return true;
        }
        return false;
    }

    private synchronized void updateLogMap() {
        final File file = new File(getLogFilePath());
        if (file.exists()) {
            if (hasChanged(file)) {
                LOG.debug("update map from file");
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
        } else {
            logsMap.clear();
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
