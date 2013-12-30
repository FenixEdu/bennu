package org.fenixedu.bennu.scheduler.domain;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.scheduler.SchedulerConfiguration;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.io.Files;

public class SchedulerSystem extends SchedulerSystem_Base {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerSystem.class);
    private static final Map<String, Task> tasks = new HashMap<>();

    private static Scheduler scheduler;
    public static LinkedBlockingQueue<TaskRunner> queue;
    public static Set<TaskRunner> runningTasks;
    public static Set<TaskSchedule> scheduledTasks;
    private static final List<Thread> activeConsumers = new ArrayList<>();

    private static transient Integer leaseTime;
    private static transient Integer queueThreadsNumber;

    static {
        queue = new LinkedBlockingQueue<>();
        runningTasks = Collections.newSetFromMap(new ConcurrentHashMap<TaskRunner, Boolean>());
        scheduledTasks = Collections.newSetFromMap(new ConcurrentHashMap<TaskSchedule, Boolean>());
        getLeaseTimeMinutes();
        getQueueThreadsNumber();
    }

    /**
     * The scheduler will wait this value till next attempt to run scheduler.
     * This value must be greater than 1.
     * 
     * @return reattempt scheduler initialization time (in minutes)
     */
    private static Integer getLeaseTimeMinutes() {
        if (leaseTime == null) {
            final Integer leaseTimeProperty = SchedulerConfiguration.getConfiguration().leaseTimeMinutes();
            if (leaseTimeProperty < 2) {
                throw new Error("property scheduler.lease.time.minutes must be a positive integer greater than 1.");
            }
            LOG.info("scheduler.lease.time.minutes: {}", leaseTimeProperty);
            leaseTime = leaseTimeProperty;
        }
        return leaseTime;
    }

    /**
     * Number of threads that are processing the queue of threads
     * 
     * @return number of threads
     */
    private static Integer getQueueThreadsNumber() {
        if (queueThreadsNumber == null) {
            final Integer queueThreadsNumberProperty = SchedulerConfiguration.getConfiguration().queueThreadsNumber();
            if (queueThreadsNumberProperty < 1) {
                throw new Error("property scheduler.queue.threads.number must be a positive integer greater than 0.");
            }
            LOG.info("scheduler.queue.threads.number: {}", queueThreadsNumberProperty);
            queueThreadsNumber = queueThreadsNumberProperty;
        }
        return queueThreadsNumber;
    }

    private SchedulerSystem() {
        super();
        setBennu(Bennu.getInstance());
        File tmp = Files.createTempDir();
        setLoggingStorage(FileStorage.createNewFileSystemStorage("schedulerSystemLoggingStorage", tmp.getAbsolutePath(), 0));
        LOG.info("Create sensible default {} for logging storage", tmp.getAbsolutePath());
    }

    public static SchedulerSystem getInstance() {
        if (Bennu.getInstance().getSchedulerSystem() == null) {
            return initialize();
        }
        return Bennu.getInstance().getSchedulerSystem();
    }

    @Atomic(mode = TxMode.WRITE)
    private static SchedulerSystem initialize() {
        if (Bennu.getInstance().getSchedulerSystem() == null) {
            return new SchedulerSystem();
        }
        return Bennu.getInstance().getSchedulerSystem();
    }

    @Override
    public Set<TaskSchedule> getTaskScheduleSet() {
        // FIXME: remove when the framework support read-only slots
        return super.getTaskScheduleSet();
    }

    /**
     * 
     * @return true if scheduler is running in this server instance.
     */
    public static Boolean isRunning() {
        return isActive() && scheduler.isStarted();
    }

    /**
     * 
     * @return true if this server instance is responsible for running the scheduler.
     */
    public static Boolean isActive() {
        return scheduler != null;
    }

    @Atomic(mode = TxMode.WRITE)
    private Boolean shouldRun() {
        if (isLeaseExpired()) {
            lease();
            return true;
        }
        return false;
    }

    /**
     * Set's lease time
     * 
     * @return
     */
    private DateTime lease() {
        setLease(new DateTime());
        return getLease();
    }

    /**
     * True if lease is expired
     * 
     * @return
     */
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
            @Atomic(mode = TxMode.READ)
            public void run() {
                setShouldRun(SchedulerSystem.getInstance().shouldRun());
                if (shouldRun) {
                    bootstrap();
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

    /**
     * Initializes the scheduler.
     */
    private static void bootstrap() {
        LOG.info("Running Scheduler bootstrap");
        if (scheduler == null) {
            scheduler = new Scheduler();
            scheduler.setDaemon(true);
        }
        if (!scheduler.isStarted()) {
            cleanNonExistingSchedules();
            initSchedules();
            spawnConsumers();
            spawnRefreshSchedulesTask();
            spawnLeaseTimerTask();
            scheduler.start();
        }
    }

    /**
     * If the scheduler is initialized schedules the task that updates the lease time every getLeaseTimeMinutes() / 2 minutes
     */
    private static void spawnLeaseTimerTask() {
        int period = getLeaseTimeMinutes() * 60 * 1000 / 2;
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isRunning()) {
                    lease();
                }
            }

            @Atomic(mode = TxMode.WRITE)
            private void lease() {
                final DateTime lease = SchedulerSystem.getInstance().lease();
                LOG.debug("Leasing until {}", lease);
            }

        }, 0, period);
    }

    /**
     * Schedules or unschedules task schedules if created or deleted not in this server instance.
     * Runs every minute.
     */
    private static void spawnRefreshSchedulesTask() {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {

            @Override
            @Atomic(mode = TxMode.READ)
            public void run() {
                LOG.debug("Running refresh schedules");
                Set<TaskSchedule> domainSchedules = new HashSet<>(getInstance().getTaskScheduleSet());
                for (TaskSchedule schedule : domainSchedules) {
                    if (!schedule.isScheduled()) {
                        LOG.debug("New schedule not scheduled before {} {}", schedule.getExternalId(),
                                schedule.getTaskClassName());
                        schedule(schedule);
                    }
                }
                for (TaskSchedule schedule : scheduledTasks) {
                    if (!domainSchedules.contains(schedule)) {
                        LOG.debug("schedule disappeared not unscheduled before {} {}", schedule.getExternalId());
                        unschedule(schedule);
                    }
                }
                LOG.debug("Refresh schedules done");
            }

        }, 0, 1 * 60 * 1000);
    }

    private static void spawnConsumers() {
        for (int i = 1; i <= getQueueThreadsNumber(); i++) {
            LOG.info("Launching queue consumer {}", i);
            Thread thread = new Thread(new ProcessQueue());
            thread.start();
            activeConsumers.add(thread);
        }
    }

    /**
     * If some task was deleted of the codebase, delete current schedules referencing it.
     */
    @Atomic(mode = TxMode.WRITE)
    private static void cleanNonExistingSchedules() {
        Set<TaskSchedule> scheduleSet = new HashSet<>(SchedulerSystem.getInstance().getTaskScheduleSet());
        for (TaskSchedule schedule : scheduleSet) {
            if (!tasks.containsKey(schedule.getTaskClassName())) {
                LOG.warn("Class {} is no longer available. schedule {} - {} - {} deleted. ", schedule.getTaskClassName(),
                        schedule.getExternalId(), schedule.getTaskClassName(), schedule.getSchedule());
                schedule.delete();
            }
        }
    }

    /**
     * Add to scheduler all existing tasks schedules.
     */
    @Atomic(mode = TxMode.READ)
    private static void initSchedules() {
        for (TaskSchedule schedule : SchedulerSystem.getInstance().getTaskScheduleSet()) {
            schedule(schedule);
        }
    }

    /**
     * Schedules a task.
     * If the task is not already queued and is not running add it to the processing queue.
     * ProcessQueue threads will run pending tasks.
     * 
     * @param schedule
     */
    @Atomic(mode = TxMode.READ)
    public static void schedule(final TaskSchedule schedule) {
        if (isActive()) {
            if (schedule.isRunOnce()) {
                runNow(schedule);
            } else {
                LOG.debug("schedule [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
                schedule.setTaskId(scheduler.schedule(schedule.getSchedule(), new Runnable() {

                    @Override
                    @Atomic(mode = TxMode.READ)
                    public void run() {
                        final TaskRunner taskRunner = schedule.getTaskRunner();
                        queue(taskRunner);
                    }
                }));
                scheduledTasks.add(schedule);
            }
        } else {
            LOG.debug("don't schedule [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
        }
    }

    /**
     * Remove schedule from the scheduler. This will not delete the TaskSchedule, only removes the scheduling.
     * 
     * @param schedule
     */
    public static void unschedule(TaskSchedule schedule) {
        if (isActive()) {
            if (schedule.isRunOnce()) {
                LOG.debug("unschedule run once {}. delete it.", schedule.getTaskClassName());
                schedule.delete();
            } else {
                LOG.debug("unschedule [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
                scheduler.deschedule(schedule.getTaskId());
                scheduledTasks.remove(schedule);
            }
        } else {
            LOG.debug("don't unschedule [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private static void runNow(TaskSchedule schedule) {
        LOG.debug("run once schedule {}", schedule.getTaskClassName());
        queue(schedule.getTaskRunner());
        unschedule(schedule);
    }

    public static final void addTask(String className, Task taskAnnotation) {
        LOG.debug("Register Task : {} with name {}", className, taskAnnotation.englishTitle());
        tasks.put(className, taskAnnotation);
    }

    /**
     * When context is gracefully destroyed, set the lease time to null so that any other server instance
     * can start the scheduler.
     */

    @Atomic(mode = TxMode.WRITE)
    private static void resetLease() {
        LOG.debug("Reset lease to null");
        SchedulerSystem.getInstance().setLease(null);
    }

    public static void destroy() {
        if (isActive()) {
            LOG.info("stopping scheduler");
            scheduler.stop();
            for (final Thread consumer : activeConsumers) {
                LOG.info("interrupted consumer thread {}", consumer.getName());
                consumer.interrupt();
            }
            resetLease();
        }
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

    /**
     * Used by CronTask to store tasks' output files and custom logging.
     * 
     * @return the physical absolute path of the logging storage.
     */

    @Atomic(mode = TxMode.READ)
    public static String getLogsPath() {
        if (getInstance().getLoggingStorage() == null) {
            throw new Error("Please add logging storage");
        }
        return getInstance().getLoggingStorage().getAbsolutePath();
    }

    @Atomic(mode = TxMode.READ)
    public static void queue(final TaskRunner taskRunner) {
        synchronized (queue) {
            if (!queue.contains(taskRunner)) {
                if (!runningTasks.contains(taskRunner)) {
                    LOG.debug("Add to queue {}", taskRunner.getTaskName());
                    try {
                        queue.put(taskRunner);
                    } catch (InterruptedException e) {
                        LOG.warn("Thread was interrupted.");
                        Thread.currentThread().interrupt();
                    }
                } else {
                    LOG.debug("Don't add to queue. Task is running {}", taskRunner.getTaskName());
                }
            } else {
                LOG.debug("Don't add to queue. Already exists {}", taskRunner.getTaskName());
            }
        }
    }
}
