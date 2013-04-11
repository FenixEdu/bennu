/*
 * @(#)Scheduler.java
 *
 * Copyright 2012 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Scheduler Module.
 *
 *   The Scheduler Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Scheduler Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Scheduler Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.core.domain.scheduler;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.backend.util.LockManager;
import pt.ist.bennu.core._development.PropertiesManager;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class Scheduler extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private static final String LOCK_VARIABLE = Scheduler.class.getName();

    private static volatile Scheduler instance = null;

    // Public API starts here

    /*
     * The method is synchronized so no two threads will create instances of
     * Scheduler
     */
    public synchronized static void initialize() {
        if (instance != null) {
            return;
        }

        final String scheduleSystemFlag = PropertiesManager.getProperty("schedule.system");
        if (scheduleSystemFlag == null || scheduleSystemFlag.isEmpty() || !scheduleSystemFlag.equalsIgnoreCase("active")) {
            // SchedulerSystem.getInstance().clearAllScheduledTasks();
        } else {
            pt.ist.fenixframework.plugins.scheduler.Scheduler.initialize();
        }

        try {
            Task.initTasks();
            instance = new Scheduler();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            throw new Error(e);
        } catch (final InstantiationException e) {
            e.printStackTrace();
            throw new Error(e);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static void shutdown() {
        instance.timer.cancel();
    }

    // Internals

    private final Timer timer = new Timer(true);

    private Scheduler() {
        timer.scheduleAtFixedRate(this, 10000, 20000);
    }

    @Override
    public void run() {
        try {
            queueTasks();
            runPendingTask();
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @Atomic
    private void queueTasks() {
        final PendingExecutionTaskQueue queue = PendingExecutionTaskQueue.getPendingExecutionTaskQueue();
        for (final Task task : MyOrg.getInstance().getTasksSet()) {
            if (queue.contains(task)) {
                continue;
            }
            for (final TaskConfiguration taskConfiguration : task.getTaskConfigurationsSet()) {
                if (taskConfiguration.shouldRunNow()) {
                    queue.offer(task);
                    break;
                }
            }
        }
    }

    @Atomic(mode = TxMode.READ)
    private void runPendingTask() {
        try {
            logger.debug("Running Scheduler");
            if (LockManager.acquireDistributedLock(LOCK_VARIABLE)) {
                logger.debug("Lock acquired, running pending tasks.");
                PendingExecutionTaskQueue.runPendingTask();
            } else {
                logger.debug("Not running pending task, another server already running!");
            }
        } finally {
            LockManager.releaseDistributedLock(LOCK_VARIABLE);
            logger.debug("Released Lock");
        }
    }

}
