package org.fenixedu.bennu.scheduler.example;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.joda.time.DateTime;

@Task(englishTitle = "This task runs every hour")
public class HourTask extends CronTask {

    @Override
    public void runTask() {
        System.out.println("Esta corre todas as horas : " + new DateTime());
    }

}
