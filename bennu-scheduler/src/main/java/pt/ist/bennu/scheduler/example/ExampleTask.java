package pt.ist.bennu.scheduler.example;

import org.joda.time.DateTime;

import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;

@Task(englishTitle = "This task runs every minutes")
public class ExampleTask extends CronTask {

    @Override
    public void runTask() {
        System.out.println("Esta corre todos os minutos : " + new DateTime());
    }

}
