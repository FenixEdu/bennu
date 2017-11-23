package org.fenixedu.bennu.scheduler.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * This log repository implementation stores the execution logs in the file system, scattering them in the following way:
 * 
 * <pre>
 * ├ index.json
 * ├ org_fenixedu_bennu_scheduler_example_ExampleTask
 * │ ├── 4ea3e6
 * │ │   └── e0f643
 * │ │       └── 4cc7b2
 * │ │           └── 381e01
 * │ │               └── 4ea6f7
 * │ │                   └── 1d
 * │ │                       ├── execution.json
 * │ │                       ├── files
 * │ │                       │   ├── 02bbcba853c0c09a8df49feaf6799842
 * │ │                       └── output
 * </pre>
 * 
 * In the root of the repository, an {@code index.json} file contains the latest execution id for every different task.
 * 
 * For every execution, a set of folders is generated based on the execution's id, dispersing the id among multiple
 * sub-directories (in this case, each sub-directory has 6-character names, meaning the repository is configured with a dispersion
 * factor of 6).
 * 
 * Inside each execution's folder, there are three main components:
 * <ul>
 * <li>An {@code execution.json} file, containing the JSON for the associated {@link ExecutionLog}, as well as the id for the
 * previous execution of this task. This effectively creates a linked-list of executions with O(1) insertion complexity.</li>
 * <li>An optional {@code output} file, containing the task log, if it is created by the task.</li>
 * <li>A {@code files} folder, containing all the files added to by the task during its execution. To prevent path-traversal
 * attacks, the filename is hashed.</li>
 * </ul>
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class FileSystemLogRepository implements ExecutionLogRepository {

    private static final JsonParser parser = new JsonParser();

    private final String basePath;
    private final int dispersionFactor;

    /**
     * Creates a new {@link FileSystemLogRepository} with the given dispersion factor, and the given base path.
     * 
     * The dispersion factor is used to determine the maximum length that each execution's folder name has, thus dispersing the
     * log files in a file tree, instead of a flat list.
     * 
     * @param basePath
     *            The base path to use to store the logs
     * @param dispersionFactor
     *            The dispersion factor to be used
     */
    public FileSystemLogRepository(String basePath, int dispersionFactor) {
        this.basePath = basePath;
        this.dispersionFactor = dispersionFactor;
    }

    /**
     * Creates a new {@link FileSystemLogRepository} with the given dispersion factor, and the default logs path.
     * 
     * @param dispersionFactor
     *            The dispersion factor to be used
     */
    public FileSystemLogRepository(int dispersionFactor) {
        this(SchedulerSystem.getLogsPath(), dispersionFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ExecutionLog log) {
        store(log,
                readJson(logFileFor(log.getTaskName(), log.getId())).map(obj -> obj.getAsJsonPrimitive("previous")).map(
                        JsonPrimitive::getAsString));
    }

    private void store(ExecutionLog log, Optional<String> previous) {
        JsonObject json = log.json();
        previous.ifPresent(prev -> json.addProperty("previous", prev));
        write(logFileFor(log.getTaskName(), log.getId()), json.toString().getBytes(StandardCharsets.UTF_8), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void newExecution(ExecutionLog log) {
        synchronized (this) {
            JsonObject json = readIndexJson();
            Optional<String> previous =
                    Optional.ofNullable(json.getAsJsonPrimitive(log.getTaskName())).map(JsonPrimitive::getAsString);
            json.addProperty(log.getTaskName(), log.getId());
            store(log, previous);
            write(indexFilePath(), json.toString().getBytes(StandardCharsets.UTF_8), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendTaskLog(ExecutionLog log, String text) {
        write(outputFileFor(log.getTaskName(), log.getId()), text.getBytes(StandardCharsets.UTF_8), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeFile(ExecutionLog log, String fileName, byte[] contents, boolean append) {
        write(fullPathFor(log.getTaskName(), log.getId(), fileName), contents, append);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<ExecutionLog> latest() {
        return readIndexJson().entrySet().stream()
                .map(entry -> getLog(entry.getKey(), entry.getValue().getAsString()).orElse(null)).filter(Objects::nonNull);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<ExecutionLog> executionsFor(String taskName, Optional<String> start, int max) {
        String id;
        if (start.isPresent()) {
            id = start.get();
        } else {
            JsonObject index = readIndexJson();
            if (!index.has(taskName)) {
                return Stream.empty();
            }
            id = index.get(taskName).getAsString();
        }
        List<ExecutionLog> logs = new ArrayList<>(Math.min(max, 100));
        while (id != null && max > 0) {
            Optional<JsonObject> optional = readJson(logFileFor(taskName, id));
            if (optional.isPresent()) {
                JsonObject json = optional.get();
                id = json.has("previous") ? json.get("previous").getAsString() : null;
                logs.add(new ExecutionLog(json));
                max--;
            } else {
                break;
            }
        }
        return logs.stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getTaskLog(String taskName, String id) {
        return read(outputFileFor(taskName, id)).map(bytes -> new String(bytes, StandardCharsets.UTF_8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<byte[]> getFile(String taskName, String id, String fileName) {
        return read(fullPathFor(taskName, id, fileName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ExecutionLog> getLog(String taskName, String id) {
        return readJson(logFileFor(taskName, id)).map(ExecutionLog::new);
    }

    // Helpers

    private JsonObject readIndexJson() {
        return readJson(indexFilePath()).orElseGet(JsonObject::new);
    }

    private String indexFilePath() {
        return basePath + "/index.json";
    }

    private String fullPathFor(String taskName, String id, String fileName) {
        return basePathFor(taskName, id) + "/files/" + Hashing.sha1().hashString(fileName, StandardCharsets.UTF_8).toString();
    }

    private String logFileFor(String taskName, String id) {
        return basePathFor(taskName, id) + "/execution.json";
    }

    private String outputFileFor(String taskName, String id) {
        return basePathFor(taskName, id) + "/output";
    }

    private String basePathFor(String taskName, String id) {
        return basePath + "/" + taskName.replace('.', '_') + "/"
                + Joiner.on('/').join(Splitter.fixedLength(dispersionFactor).split(id));
    }

    // Readers

    private static void write(String path, byte[] bytes, boolean append) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(file, append)) {
            stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Optional<byte[]> read(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.toByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<JsonObject> readJson(String path) {
        final Optional<JsonElement> e = read(path).map(bytes -> parser.parse(new String(bytes, StandardCharsets.UTF_8)));
        return e.filter(o -> !o.isJsonNull()).map(JsonElement::getAsJsonObject);
    }

}
