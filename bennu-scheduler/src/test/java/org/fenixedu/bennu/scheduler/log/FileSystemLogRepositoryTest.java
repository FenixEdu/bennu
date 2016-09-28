package org.fenixedu.bennu.scheduler.log;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.io.Files;

@RunWith(JUnit4.class)
public class FileSystemLogRepositoryTest {

    private static final String TASK_NAME = FileSystemLogRepositoryTest.class.getName();

    private ExecutionLogRepository repository;

    @Before
    public void setupRepository() {
        File file = Files.createTempDir();
        file.deleteOnExit();
        repository = new FileSystemLogRepository(file.getAbsolutePath(), 6);
    }

    @Test
    public void testUpdate() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(original);

        assertEquals(original, repository.getLog(original.getTaskName(), original.getId()).get());

        ExecutionLog log = original.withSuccess();
        repository.update(log);

        assertEquals(log, repository.getLog(original.getTaskName(), original.getId()).get());
    }

    @Test
    public void testLatest() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(original);
        assertTrue(repository.latest().allMatch(l -> l.equals(original)));

        ExecutionLog newLog = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog);
        assertTrue(repository.latest().allMatch(l -> l.equals(newLog)));
    }

    @Test
    public void testExecutionsFor() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(original);
        ExecutionLog newLog = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog);
        assertTrue(repository.executionsFor(original.getTaskName(), Optional.empty(), Integer.MAX_VALUE).anyMatch(
                l -> l.equals(original)));
        assertTrue(repository.executionsFor(newLog.getTaskName(), Optional.empty(), Integer.MAX_VALUE).anyMatch(
                l -> l.equals(newLog)));
    }

    @Test
    public void testExecutionsForWithSpecificStartId() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(original);
        ExecutionLog newLog1 = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog1);
        ExecutionLog newLog2 = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog2);
        ExecutionLog newLog3 = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog3);

        assertEquals(repository.executionsFor(TASK_NAME, Optional.empty(), Integer.MAX_VALUE).collect(Collectors.toSet()),
                repository.executionsFor(TASK_NAME, Optional.of(newLog3.getId()), Integer.MAX_VALUE).collect(Collectors.toSet()));
        assertEquals(4, repository.executionsFor(TASK_NAME, Optional.of(newLog3.getId()), Integer.MAX_VALUE).count());
        assertEquals(Arrays.asList(newLog3, newLog2, newLog1, original),
                repository.executionsFor(TASK_NAME, Optional.of(newLog3.getId()), Integer.MAX_VALUE).collect(Collectors.toList()));

        assertEquals(3, repository.executionsFor(TASK_NAME, Optional.of(newLog2.getId()), Integer.MAX_VALUE).count());
        assertEquals(Arrays.asList(newLog2, newLog1, original),
                repository.executionsFor(TASK_NAME, Optional.of(newLog2.getId()), Integer.MAX_VALUE).collect(Collectors.toList()));

        assertEquals(2, repository.executionsFor(TASK_NAME, Optional.of(newLog1.getId()), Integer.MAX_VALUE).count());
        assertEquals(Arrays.asList(newLog1, original),
                repository.executionsFor(TASK_NAME, Optional.of(newLog1.getId()), Integer.MAX_VALUE).collect(Collectors.toList()));

        assertEquals(1, repository.executionsFor(TASK_NAME, Optional.of(original.getId()), Integer.MAX_VALUE).count());
        assertEquals(Arrays.asList(original),
                repository.executionsFor(TASK_NAME, Optional.of(original.getId()), Integer.MAX_VALUE)
                        .collect(Collectors.toList()));
    }

    @Test
    public void testExecutionLogsWithInvalidId() {
        ExecutionLog original = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(original);
        ExecutionLog newLog1 = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog1);
        ExecutionLog newLog2 = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog2);
        ExecutionLog newLog3 = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(newLog3);

        assertEquals("The returned stream is not empty", 0,
                repository.executionsFor(TASK_NAME, Optional.of("XPTO"), Integer.MAX_VALUE).count());
    }

    @Test
    public void testFindLog() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(log);
        assertEquals(Optional.of(log), repository.getLog(log.getTaskName(), log.getId()));
    }

    @Test
    public void testTaskLog() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(log);
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, 100).forEach(i -> {
            repository.appendTaskLog(log, String.valueOf(i));
            builder.append(i);
        });
        assertEquals(builder.toString(), repository.getTaskLog(log.getTaskName(), log.getId()).get());
    }

    @Test
    public void testFileOutput() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(log);
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        repository.storeFile(log, "myfile", bytes, false);
        assertArrayEquals(bytes, repository.getFile(log.getTaskName(), log.getId(), "myfile").get());
    }

    @Test
    public void testFileAppend() {
        ExecutionLog log = ExecutionLog.newExecutionFor(TASK_NAME);
        repository.newExecution(log);
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        {
            byte[] subArray = new byte[32];
            System.arraycopy(bytes, 0, subArray, 0, 32);
            repository.storeFile(log, "myfile", subArray, false);
        }
        {
            byte[] subArray = new byte[32];
            System.arraycopy(bytes, 32, subArray, 0, 32);
            repository.storeFile(log, "myfile", subArray, true);
        }
        assertArrayEquals(bytes, repository.getFile(log.getTaskName(), log.getId(), "myfile").get());
    }
}
