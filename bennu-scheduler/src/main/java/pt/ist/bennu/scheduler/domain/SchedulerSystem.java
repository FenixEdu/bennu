package pt.ist.bennu.scheduler.domain;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import jvstm.TransactionalCommand;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.scheduler.annotation.Task;
import pt.ist.fenixframework.pstm.Transaction;

public class SchedulerSystem extends SchedulerSystem_Base {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerSystem.class);
    private static final Map<String, Task> tasks = new HashMap<>();

    private static Scheduler scheduler;
    public static LinkedBlockingQueue<TaskRunner> queue;

    private static final Integer DEFAULT_LEASE_TIME_MINUTES = 5;
    private static final Integer DEFAULT_QUEUE_THREADS_NUMBER = 2;

    static {
        queue = new LinkedBlockingQueue<>();
    }

    /**
     * The scheduler will wait this value till next attempt to run scheduler.
     * This value must be greater than 1.
     * 
     * @return reattempt scheduler initialization time (in minutes)
     */
    private static Integer getLeaseTimeMinutes() {
        final Integer leaseTime =
                ConfigurationManager.getIntegerProperty("scheduler.lease.time.minutes", DEFAULT_LEASE_TIME_MINUTES);
        if (leaseTime < 2) {
            throw new Error("property scheduler.lease.time.minutes must be a positive integer greater than 1.");
        }
        return leaseTime;
    }

    /**
     * Number of threads that are processing the queue of threads
     * 
     * @return number of threads
     */
    private static Integer getQueueThreadsNumber() {
        final Integer queueThreadsNumber =
                ConfigurationManager.getIntegerProperty("scheduler.queue.threads.number", DEFAULT_QUEUE_THREADS_NUMBER);
        if (queueThreadsNumber < 1) {
            throw new Error("property scheduler.queue.threads.number must be a positive integer greater than 0.");
        }
        return queueThreadsNumber;
    }

    private SchedulerSystem() {
        super();
    }

    public static SchedulerSystem getInstance() {
        if (!Bennu.getInstance().hasSchedulerSystem()) {
            Bennu.getInstance().setSchedulerSystem(new SchedulerSystem());
        }
        return Bennu.getInstance().getSchedulerSystem();
    }

    private Boolean shouldRun() {
        if (isLeaseExpired()) {
            lease();
            return true;
        }
        return false;
    }

    private DateTime lease() {
        setLease(new DateTime());
        return getLease();
    }

    private boolean isLeaseExpired() {
        final DateTime lease = getLease();
        if (lease == null) {
            return true;
        }
        return lease.plusMinutes(getLeaseTimeMinutes()).isBeforeNow();
    }

    /***
     * This method starts the scheduler if lease is expired.
     * */
    public static void init() {

        new Timer(true).scheduleAtFixedRate(new TimerTask() {

            Boolean shouldRun = false;

            @Override
            public void run() {
                Transaction.withTransaction(false, new TransactionalCommand() {

                    @Override
                    public void doIt() {
                        setShouldRun(SchedulerSystem.getInstance().shouldRun());
                    }
                });
                if (shouldRun) {
                    LOG.debug("Running bootstrap");
                    Transaction.withTransaction(false, new TransactionalCommand() {

                        @Override
                        public void doIt() {
                            bootstrap();
                        }
                    });
                } else {
                    LOG.debug("Lease is not gone. Wait for it ...");
                }
                setShouldRun(false);
            }

            public void setShouldRun(Boolean shouldRun) {
                this.shouldRun = shouldRun;
            }

        }, 0, getLeaseTimeMinutes() * 60 * 1000);

    }

    private static void bootstrap() {
        if (scheduler == null) {
            scheduler = new Scheduler();
            scheduler.setDaemon(true);
        }
        if (!scheduler.isStarted()) {
            cleanNonExistingSchedules();
            initSchedules();
            spawnConsumers();
            scheduler.start();
        }
    }

    private static void spawnConsumers() {
        final ExecutorService threadPool = Executors.newFixedThreadPool(getQueueThreadsNumber());

        for (int i = 1; i <= getQueueThreadsNumber(); i++) {
            LOG.info("Launching queue consumer {}", i);
            threadPool.execute(new ProcessQueue());
        }

    }

    private static void cleanNonExistingSchedules() {
        for (TaskSchedule schedule : SchedulerSystem.getInstance().getTaskSchedule()) {
            if (!tasks.containsKey(schedule.getTaskClassName())) {
                LOG.warn("Class {} is no longer available. schedule {} - {} - {} deleted. ", schedule.getTaskClassName(),
                        schedule.getExternalId(), schedule.getTaskClassName(), schedule.getSchedule());
                schedule.delete();
            }
        }

    }

    private static void initSchedules() {
        for (TaskSchedule schedule : SchedulerSystem.getInstance().getTaskSchedule()) {
            schedule(schedule);
        }

        scheduler.schedule(String.format("*/%d * * * *", getLeaseTimeMinutes() / 2), new Runnable() {
            @Override
            public void run() {
                Transaction.withTransaction(false, new TransactionalCommand() {

                    @Override
                    public void doIt() {
                        final DateTime lease = SchedulerSystem.getInstance().lease();
                        LOG.info("Leasing until {}", lease);
                    }
                });
            }
        });
    }

    public static void schedule(final TaskSchedule schedule) {
        LOG.info("schedule [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
        schedule.setTaskId(scheduler.schedule(schedule.getSchedule(), new Runnable() {

            @Override
            public void run() {
                synchronized (queue) {
                    try {
                        final TaskRunner taskRunner = schedule.getTaskRunner();
                        if (!queue.contains(taskRunner)) {
                            LOG.info("Add to queue {}", taskRunner.getTaskName());
                            queue.put(taskRunner);
                        } else {
                            LOG.info("Don't add to queue. Already exists {}", taskRunner.getTaskName());
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        }));
    }

    public static void unschedule(TaskSchedule schedule) {
        LOG.info("unschedule [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
        scheduler.deschedule(schedule.getTaskId());
    }

    public static final void addTask(String className, Task taskAnnotation) {
        LOG.info("Register Task : {} with name {}", className, taskAnnotation.englishTitle());
        tasks.put(className, taskAnnotation);
    }

    public static void destroy() {
        Transaction.withTransaction(false, new TransactionalCommand() {

            @Override
            public void doIt() {
                LOG.info("Revert lease to null");
                SchedulerSystem.getInstance().setLease(null);
            }
        });
    }

    public static Map<String, Task> getTasks() {
        return tasks;
    }

    public static String getTaskName(String className) {
        final Task taskAnnotation = tasks.get(className);
        if (taskAnnotation != null) {
            return taskAnnotation.englishTitle();
        }
        return null;
    }
}
