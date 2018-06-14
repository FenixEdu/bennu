package org.fenixedu.bennu.scheduler.future;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "This task executes Persistent Future that have yet to be executed")
public class ExecuteFutureTask extends CronTask {

    @Override
    public void runTask() {
        try {
            FutureSystem.getInstance().getIncompletePersistentFutureSet().stream().findAny().ifPresent((pf) -> {
                pf.execute();
            });
        } catch (final Exception e) {
            final StringWriter stacktrace = new StringWriter();
            try (PrintWriter writer = new PrintWriter(stacktrace)) {
                e.printStackTrace(writer);
            }

            taskLog(stacktrace.toString());
            getLogger().error(stacktrace.toString());
        }
    }
}
