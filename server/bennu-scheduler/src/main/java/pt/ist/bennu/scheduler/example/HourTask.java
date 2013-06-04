package pt.ist.bennu.scheduler.example;

import org.joda.time.DateTime;

import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;

@Task(englishTitle = "This task runs every hour")
public class HourTask extends CronTask {

    @Override
    public void runTask() {
        System.out.println("Esta corre todas as horas : " + new DateTime());
    }

}
