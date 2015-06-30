package org.fenixedu.bennu.scheduler.example;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "This task runs every minutes")
public class ExampleTask extends CronTask {

    private SecureRandom random = new SecureRandom();

    @Override
    public void runTask() {
        final int randInt = random.nextInt(100);
        if (randInt > 50) {
            throw new AssertionError("Random Int Exception: " + randInt, new Exception("Just a cause"));
        }
        for (int i = 0; i < 32; i++) {
            final String filename = next();
            taskLog("Writing number %d to file %s\n", i, filename);
            output(filename, new Integer(i).toString().getBytes());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String next() {
        return new BigInteger(130, random).toString(32) + ".txt";
    }
}
