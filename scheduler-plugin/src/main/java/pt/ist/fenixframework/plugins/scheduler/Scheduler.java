package pt.ist.fenixframework.plugins.scheduler;

import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.backend.util.LockManager;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.plugins.scheduler.domain.SchedulerSystem;

public class Scheduler extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private static final int SCHEDULER_INVOCATION_PERIOD = 120000; // (ms)

    public static void initialize() {
        new Scheduler();
    }

    private final Timer timer = new Timer(true);

    public Scheduler() {
        final DateTime dt = new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).plusMinutes(1);
        timer.scheduleAtFixedRate(this, dt.toDate(), SCHEDULER_INVOCATION_PERIOD);
    }

    @Override
    public void run() {
        try {
            SchedulerSystem.queueTasks();
            runPendingTask();
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @Atomic(mode = TxMode.READ)
    private void runPendingTask() {
        final String lockVariable = SchedulerSystem.class.getName();

        try {
            logger.debug("Running Scheduler");
            if (LockManager.acquireDistributedLock(lockVariable)) {
                SchedulerSystem.runPendingTask();
            } else {
                logger.debug("Not running pending task, another server already running!");
            }
        } finally {
            LockManager.releaseDistributedLock(lockVariable);
            logger.debug("Releasing Lock");
        }
    }

}
