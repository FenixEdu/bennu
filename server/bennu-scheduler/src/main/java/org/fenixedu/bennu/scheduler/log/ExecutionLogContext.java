package org.fenixedu.bennu.scheduler.log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class ExecutionLogContext {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionLogContext.class);

    private Table<String, String, JsonObject> logsTable;
    
    private Map<String, String> lastIdForTaskNameMap;
    
    
    private Long lastModified;
    
    public ExecutionLogContext() {
        logsTable = HashBasedTable.create();
        lastModified = Long.MIN_VALUE;
        lastIdForTaskNameMap = new ConcurrentHashMap<>();
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
    
    private synchronized void updateLogTable() {
        final File file = new File(getLogFilePath());
        if (file.exists()) {
            if (hasChanged(file)) {
                LOG.debug("update map from file");
                try (FileReader fileReader = new FileReader(file)) {
                    JsonStreamParser parser = new JsonStreamParser(fileReader);
                    while (parser.hasNext()) {
                        final JsonObject jsonLog = parser.next().getAsJsonObject();
                        final String jsonLogId = jsonLog.get("id").getAsString();
                        final String jsonLogTaskName = jsonLog.get("taskName").getAsString();
                        final JsonObject jsonLogFromTable = logsTable.get(jsonLogTaskName, jsonLogId);
                        if (jsonLogFromTable != null) {
                            update(jsonLog, jsonLogFromTable);
                        } else {
                        	logsTable.put(jsonLogTaskName, jsonLogId, jsonLog);
                        	lastIdForTaskNameMap.put(jsonLogTaskName, jsonLogId);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            logsTable.clear();
        }
    }

    private String getLogFilePath() {
        return SchedulerSystem.getLogsPath().concat(getLogFileName());
    }

    protected String getLogFileName() {
        return ExecutionLog.LOG_JSON_FILENAME;
    }

    private void update(JsonObject source, JsonObject target) {
        if (source.equals(target)) {
            return;
        }
        /* this rewrites json object content */
        for (final Entry<String, JsonElement> entry : source.entrySet()) {
            target.add(entry.getKey(), entry.getValue());
        }
    }

    public SortedSet<JsonObject> get(String taskName) {
    	updateLogTable();
    	return FluentIterable.from(logsTable.row(taskName).values()).toSortedSet(new Comparator<JsonObject>() {

			@Override
			public int compare(JsonObject o1, JsonObject o2) {
				return o1.get("id").getAsString().compareTo(o2.get("id").getAsString());
			}
		});
    }
    
    public JsonObject get(String taskName, String logId) {
        updateLogTable();
        return logsTable.get(taskName, logId);
    }
    
    public Collection<JsonObject> last() {
    	updateLogTable();
    	Collection<JsonObject> logs = new HashSet<>();
    	for(String taskName : lastIdForTaskNameMap.keySet()) {
    		logs.add(logsTable.get(taskName, lastIdForTaskNameMap.get(taskName)));
    	}
    	return logs;
    }
    
    public Collection<JsonObject> values() {
    	updateLogTable();
        return logsTable.values();
    }

    public Boolean isEmpty() {
    	updateLogTable();
        return logsTable.isEmpty();
    }
}
