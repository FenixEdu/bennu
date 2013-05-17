package pt.ist.bennu.scheduler.example;

import org.joda.time.DateTime;

import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;

@Task(englishTitle = "This task logs current timestamp every second till 3 minutes after started")
public class LoggingTask extends CronTask {

    @Override
    public void runTask() {
        DateTime now = new DateTime();
        DateTime timestamp;
        do {
            timestamp = new DateTime();
            getLogger().info(timestamp.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (timestamp.isBefore(now.plusMinutes(3)));
    }
}
