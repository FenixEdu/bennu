package org.fenixedu.bennu.scheduler.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Optional;

import org.fenixedu.bennu.scheduler.log.ExecutionLog.TaskState;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExecutionLogTest {

    private static final String TASK_NAME = ExecutionLogTest.class.getName();

    @Test
    public void testNewExecutionLog() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);

        assertNotNull(log.getId());
        assertNotNull(datesMatch(DateTime.now(), log.getStart()));
        assertEquals(Optional.empty(), log.getEnd());
        assertEquals(TaskState.RUNNING, log.getState());
        assertEquals(TASK_NAME, log.getTaskName());
        assertEquals(Optional.empty(), log.getStackTrace());
        assertTrue(log.getFiles().isEmpty());
        assertNotNull(log.getHostname());
        assertEquals(Optional.empty(), log.getCode());
        assertEquals(Optional.empty(), log.getUser());
    }

    @Test(expected = NullPointerException.class)
    public void testNullTaskName() {
        ExecutionLog.newExecutionFor(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTaskNameOnCustomTask() {
        ExecutionLog.newCustomExecution(null, "code", "myself");
    }

    @Test(expected = NullPointerException.class)
    public void testNullCode() {
        ExecutionLog.newCustomExecution(TASK_NAME, null, "myself");
    }

    @Test(expected = NullPointerException.class)
    public void testNullTaskNamesDontWork() {
        ExecutionLog.newCustomExecution(TASK_NAME, "code", null);
    }

    private static boolean datesMatch(DateTime expected, DateTime actual) {
        return new Duration(actual, expected).getStandardSeconds() < 1;
    }

    @Test
    public void testCustomExecutionLogs() {
        ExecutionLog log = ExecutionLog.newCustomExecution(TASK_NAME, "mycode", "myself");

        assertEquals(Optional.of("mycode"), log.getCode());
        assertEquals(Optional.of("myself"), log.getUser());
    }

    @Test
    public void testSuccess() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME).withFile("xpto");
        ExecutionLog successful = log.withSuccess();

        assertEquals(log.getId(), successful.getId());
        assertEquals(log.getStart(), successful.getStart());
        datesMatch(DateTime.now(), successful.getEnd().get());
        assertEquals(TaskState.SUCCESS, successful.getState());
        assertEquals(log.getTaskName(), successful.getTaskName());
        assertEquals(log.getStackTrace(), successful.getStackTrace());
        assertEquals(log.getFiles(), successful.getFiles());
        assertEquals(log.getHostname(), successful.getHostname());
        assertEquals(log.getCode(), successful.getCode());
        assertEquals(log.getUser(), successful.getUser());
    }

    @Test
    public void testEquals() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);
        ExecutionLog custom = ExecutionLog.newCustomExecution(TASK_NAME, "mycode", "myself");

        assertEquals(log, log);
        assertNotEquals(log, log.withSuccess());
        assertNotEquals(log, log.withFile("xpto"));
        assertNotEquals(log, log.withError(new Throwable()));
        assertNotEquals(log, ExecutionLog.newExecutionFor(TASK_NAME));
        assertNotEquals(log, ExecutionLog.newExecutionFor("SOME_OTHER_TASK_NAME"));
        assertNotEquals(log, custom);

        assertNotEquals(custom, ExecutionLog.newCustomExecution(TASK_NAME, "othercode", "myself"));
        assertNotEquals(custom, ExecutionLog.newCustomExecution(TASK_NAME, "mycode", "another"));
    }

    @Test
    public void testFailure() {
        Throwable throwable = new Throwable();
        StringWriter stacktrace = new StringWriter();
        try (PrintWriter print = new PrintWriter(stacktrace)) {
            throwable.printStackTrace(print);
        }
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);
        ExecutionLog failed = log.withError(throwable);

        assertEquals(log.getId(), failed.getId());
        assertEquals(log.getStart(), failed.getStart());
        datesMatch(DateTime.now(), failed.getEnd().get());
        assertEquals(TaskState.FAILURE, failed.getState());
        assertEquals(log.getTaskName(), failed.getTaskName());
        assertEquals(stacktrace.toString(), failed.getStackTrace().get());
        assertEquals(log.getFiles(), failed.getFiles());
        assertEquals(log.getHostname(), failed.getHostname());
        assertEquals(log.getCode(), failed.getCode());
        assertEquals(log.getUser(), failed.getUser());
    }

    @Test
    public void testFiles() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME);
        ExecutionLog log = original.withFile("xpto").withFile("ptox");

        assertEquals(Collections.emptySet(), original.getFiles());
        assertEquals(2, log.getFiles().size());
        assertTrue(log.getFiles().contains("xpto"));
        assertTrue(log.getFiles().contains("ptox"));

        // Check other properties are equal

        assertEquals(original.getId(), log.getId());
        assertEquals(original.getStart(), log.getStart());
        assertEquals(original.getEnd(), log.getEnd());
        assertEquals(original.getState(), log.getState());
        assertEquals(original.getTaskName(), log.getTaskName());
        assertEquals(original.getStackTrace(), log.getStackTrace());
        assertEquals(original.getHostname(), log.getHostname());
        assertEquals(original.getCode(), log.getCode());
        assertEquals(original.getUser(), log.getUser());
    }

    @Test
    public void testJSONSerialization() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME).withFile("xpto").withError(new Throwable());
        ExecutionLog copy = new ExecutionLog(original.json());

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getStart(), copy.getStart());
        assertEquals(original.getEnd(), copy.getEnd());
        assertEquals(original.getState(), copy.getState());
        assertEquals(original.getTaskName(), copy.getTaskName());
        assertEquals(original.getStackTrace(), copy.getStackTrace());
        assertEquals(original.getFiles(), copy.getFiles());
        assertEquals(original.getHostname(), copy.getHostname());
        assertEquals(original.getCode(), copy.getCode());
        assertEquals(original.getUser(), copy.getUser());
    }
}
