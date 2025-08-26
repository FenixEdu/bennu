## [**Scheduling**](./scheduling.md)

In several web applications there is the need of running periodic tasks on the system. Bennu provides a simple way of doing so. To create tasks that can be scheduled the developer needs to implement a **CronTask** and annotate it with **@Task**, like so:

```java
@Task(englishTitle = "Logger test")
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
```

By default the task runs in write mode, to make it read-only change the annotation to:

```java
@Task(englishTitle = "Logger test", readOnly = true)
```

Inside the **runTask()** you can call methods annotated with **@Atomic(mode=TxMode.WRITE)** as in any other piece of code.

Configuring a task to run periodically is done in the application's interface specifying a crontab like schedule periodicity ([Cron](http://en.wikipedia.org/wiki/Cron))

Alternatively to developing a task in the codebase there is the possibility of submitting your code directly in the interface to run immediatly. To do so extendCustomTask instead, without annotation:

```java
public class LoggingTask extends CustomTask {
    @Override
    public void runTask() {
        ...
    }
}
```
And use the Custom Task submission interface.

## Logging
Every time a task is run, the Scheduler system keeps track of it, storing the log for the execution. In the log, the following are stored for future reference:

+ Execution ID (random string assigned to each execution)
+ Start Time
+ End Time (only if the task has already finished)
+ State (Either Running, Success or Failure)
+ Task Name (fully qualified class name)
+ Stack Trace (if an exception was thrown)
+ File list (of files explicitly output by the task)
+ Hostname for the application server that ran the task
+ Code (only for custom tasks)
+ Username of the user who ran the task (only for custom tasks)

Due to the potentially high volume of task execution, the number of log entries may increase really quickly.

### Log Repository
The Scheduler provides an extension point to customize how logs are stored. The default implementation stores the logs in a file-system tree, keeping an index of the latest execution for each task in the tree's root.

