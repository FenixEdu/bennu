package org.fenixedu.bennu.scheduler.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.stream.StreamUtils;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * An execution log is a record containing all the relevant information about a specific execution of a task.
 * 
 * Each execution is assigned an ID that is unique across all executions of the same task.
 * 
 * An execution log is immutable, meaning that different snapshots of a given execution can be taken. Newer versions of log for
 * this execution must be retrieved using the log repository.
 * 
 * It may optionally hold the code of the task that was run, as well as the user who triggered its execution. This usage is mostly
 * prevalent in manually-input {@link CustomTask}s.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class ExecutionLog {

    private static String computeHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * Represents the current state of a task.
     * 
     * Every task starts in the {@link #RUNNING} state, and when the task is complete, transitions to either the {@link #SUCCESS}
     * or {@link #FAILURE} states, depending of whether the task was successful or not.
     * 
     * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
     */
    public static enum TaskState {
        RUNNING, SUCCESS, FAILURE;
    }

    private final String id;
    private final DateTime start;
    private final Optional<DateTime> end;
    private final TaskState state;
    private final String taskName;
    private final Optional<String> stackTrace;
    private final Set<String> files;
    private final String hostname;
    private final Optional<String> code;
    private final Optional<String> user;

    /**
     * Creates a new Execution Log from the provided JSON representation.
     * 
     * The provided JSON <b>must</b> have been returned by another log's {@link #json()} method, otherwise the results of invoking
     * this constructor are undefined.
     * 
     * @param json
     *            The JSON object containing the description of an execution log
     */
    protected ExecutionLog(JsonObject json) {
        this.id = string(json, "id").get();
        this.start = string(json, "start").map(DateTime::new).get();
        this.end = string(json, "end").map(DateTime::new);
        this.state = string(json, "state").map(TaskState::valueOf).get();
        this.taskName = string(json, "taskName").get();
        this.stackTrace = string(json, "stackTrace");
        this.files =
                StreamUtils.of(json.getAsJsonArray("files")).map(file -> file.getAsJsonPrimitive().getAsString())
                        .collect(Collectors.toSet());
        this.hostname = string(json, "hostname").get();
        this.code = string(json, "code");
        this.user = string(json, "user");
    }

    private static final Optional<String> string(JsonObject json, String property) {
        return Optional.ofNullable(json.getAsJsonPrimitive(property)).map(JsonPrimitive::getAsString);
    }

    /*
     * Standard constructor with all the required fields
     */
    private ExecutionLog(String id, DateTime start, Optional<DateTime> end, TaskState state, String taskName,
            Optional<String> stackTrace, Set<String> files, String hostname, Optional<String> code, Optional<String> user) {
        super();
        this.id = id;
        this.start = start;
        this.end = end;
        this.state = state;
        this.taskName = taskName;
        this.stackTrace = stackTrace;
        this.files = files;
        this.hostname = hostname;
        this.code = code;
        this.user = user;
    }

    /**
     * Creates a new execution log for the given task, automatically assigning it a randomly-generated unique identifier.
     * 
     * The returned log is in the {@link TaskState#RUNNING} state, with the current time as the start time.
     * 
     * @param taskName
     *            The name of the task that was run
     * @return
     *         A new execution log for the given task
     * @throws NullPointerException
     *             If the task name is {@code null}
     */
    public static ExecutionLog newExecutionFor(String taskName) {
        return new ExecutionLog(UUID.randomUUID().toString().replace("-", ""), DateTime.now(), Optional.empty(),
                TaskState.RUNNING, Objects.requireNonNull(taskName), Optional.empty(), Collections.emptySet(), computeHostName(),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a new execution log for the given custom task, taking note of the code that was run, and the user who ran it.
     * 
     * Just like the {@link #newExecutionFor(String)} method, the returned log is assigned a randomly-generated unique identifier,
     * an is in the {@link TaskState#RUNNING} state, with the current time as the start time.
     * 
     * @param taskName
     *            The name of the task that was run
     * @param code
     *            The code that was run
     * @param user
     *            The user who ran the task
     * @return
     *         A new execution log for the given task
     * @throws NullPointerException
     *             If either the task name, code or user is {@code null}
     */
    public static ExecutionLog newCustomExecution(String taskName, String code, String user) {
        return new ExecutionLog(UUID.randomUUID().toString().replace("-", ""), DateTime.now(), Optional.empty(),
                TaskState.RUNNING, Objects.requireNonNull(taskName), Optional.empty(), Collections.emptySet(), computeHostName(),
                Optional.of(code), Optional.of(user));
    }

    /**
     * Returns the unique identifier of this execution.
     * 
     * @return
     *         The identifier of the execution
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date in which this execution has started.
     * 
     * @return
     *         The start date for this execution
     */
    public DateTime getStart() {
        return start;
    }

    /**
     * If this task is in the {@link TaskState#RUNNING} state, returns an empty {@link Optional}. Otherwise returns the date in
     * which this execution has ended.
     * 
     * @return
     *         The end date for this execution, may be empty
     */
    public Optional<DateTime> getEnd() {
        return end;
    }

    /**
     * Returns the {@link TaskState} for this execution.
     * 
     * @return
     *         The current state of this execution
     */
    public TaskState getState() {
        return state;
    }

    /**
     * Returns the name of the task that this execution refers to.
     * 
     * @return
     *         The name of the task being executed
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * If the task is in the {@link TaskState#RUNNING} state, returns the stack trace of the execution thrown by the task, if one
     * is present.
     * 
     * Otherwise, returns an empty {@link Optional}.
     * 
     * @return
     *         The stack trace of the exception thrown by the task, may be empty
     */
    public Optional<String> getStackTrace() {
        return stackTrace;
    }

    /**
     * Returns an immutable set of all the filenames output by the task during its execution.
     * 
     * @return
     *         The filenames of all the files output by the task
     */
    public Set<String> getFiles() {
        return files;
    }

    /**
     * Returns the canonical hostname of the machine that ran this task.
     * 
     * @return
     *         The hostname of the machine that ran the task
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * If this log refers to a {@link CustomTask}, the code that was run is returned. Otherwise, returns an empty {@link Optional}
     * .
     * 
     * @return
     *         The code that was run, may be empty
     */
    public Optional<String> getCode() {
        return code;
    }

    /**
     * If this log refers to a {@link CustomTask}, the username of the user that ran the task is returned. Otherwise, returns an
     * empty {@link Optional}.
     * 
     * @return
     *         The user who ran the task, may be empty
     */
    public Optional<String> getUser() {
        return user;
    }

    /**
     * Creates a new {@link ExecutionLog} based on this one, marking it as successful, ending in the moment of the method call.
     * 
     * This is typically invoked upon successful task completion, so that it may be recorded in the log repository.
     * 
     * @return
     *         A copy of this log marked as successful
     */
    public ExecutionLog withSuccess() {
        return new ExecutionLog(id, start, Optional.of(DateTime.now()), TaskState.SUCCESS, taskName, Optional.empty(), files,
                hostname, code, user);
    }

    /**
     * Creates a new {@link ExecutionLog} based on this one, marking it as failed, with the stack trace provided by the given
     * throwable. The returned log has its end date as the current date.
     * 
     * This is typically invoked upon failed task completion, so that it may be recorded in the log repository.
     * 
     * @param t
     *            The exception that caused this task to fail
     * @return
     *         A copy of this log marked as failure
     */
    public ExecutionLog withError(Throwable t) {
        StringWriter stacktrace = new StringWriter();
        try (PrintWriter writer = new PrintWriter(stacktrace)) {
            t.printStackTrace(writer);
        }
        return new ExecutionLog(id, start, Optional.of(DateTime.now()), TaskState.FAILURE, taskName, Optional.of(stacktrace
                .toString()), files, hostname, code, user);
    }

    /**
     * Creates a new {@link ExecutionLog} based on this one, with the given filename added to the list of files generated by the
     * task.
     * 
     * @param filename
     *            The name of the file to be added
     * @return
     *         A copy of this log with the added file
     * @throws NullPointerException
     *             If the given filename was null
     */
    public ExecutionLog withFile(String filename) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        builder.addAll(files);
        builder.add(filename);
        return new ExecutionLog(id, start, end, state, taskName, stackTrace, builder.build(), hostname, code, user);
    }

    /**
     * Returns a JSON representation of this log.
     * 
     * Optional slots are only present in the returned JSON if they are non-empty.
     * 
     * <p>
     * <b>Note:</b> The JSON returned by this method can be used by log repositories to re-create instances of
     * {@link ExecutionLog} from their JSON representation, via the package-protected {@link #ExecutionLog(JsonObject)}
     * constructor.
     * </p>
     * 
     * @return
     *         A JSON representation of this log
     */
    public JsonObject json() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("start", start.toString());
        end.ifPresent(val -> json.addProperty("end", val.toString()));
        json.addProperty("state", state.name());
        json.addProperty("taskName", taskName);
        stackTrace.ifPresent(val -> json.addProperty("stackTrace", val));
        json.add("files", files.stream().map(JsonPrimitive::new).collect(StreamUtils.toJsonArray()));
        json.addProperty("hostname", hostname);
        code.ifPresent(val -> json.addProperty("code", val));
        user.ifPresent(val -> json.addProperty("user", val));
        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, state, taskName, stackTrace, hostname, files, code, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExecutionLog) {
            ExecutionLog other = (ExecutionLog) obj;
            return Objects.equals(id, other.id) && Objects.equals(start, other.start) && Objects.equals(end, other.end)
                    && Objects.equals(state, other.state) && Objects.equals(taskName, other.taskName)
                    && Objects.equals(stackTrace, other.stackTrace) && Objects.equals(hostname, other.hostname)
                    && Objects.equals(files, other.files) && Objects.equals(code, other.code) && Objects.equals(user, other.user);
        }
        return false;
    }
}
