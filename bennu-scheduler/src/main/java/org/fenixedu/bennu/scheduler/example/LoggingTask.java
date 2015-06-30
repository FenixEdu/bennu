package org.fenixedu.bennu.scheduler.example;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.joda.time.DateTime;

@Task(englishTitle = "This task logs current timestamp every second till 3 minutes after started")
public class LoggingTask extends CronTask {

    @Override
    public void runTask() {
        DateTime now = new DateTime();
        DateTime timestamp;
        do {
            timestamp = new DateTime();
            taskLog(timestamp.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (timestamp.isBefore(now.plusMinutes(1)));
    }
}
