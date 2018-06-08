package org.fenixedu.bennu.scheduler.future;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "This task executes Persistent Future that have yet to be executed")
public class ExecuteFutureTask extends CronTask {

    @Override
    public void runTask() {
        FutureSystem.getInstance().getIncompletePersistentFutureSet().stream().findAny().ifPresent((pf) -> {
            pf.execute();
        });
    }
}
