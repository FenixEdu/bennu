package org.fenixedu.bennu.scheduler.domain;

import org.fenixedu.bennu.scheduler.TaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProcessQueue implements Runnable {
    private Logger LOG = LoggerFactory.getLogger(ProcessQueue.class);

    public ProcessQueue() {
        super();
    }

    @Override
    public void run() {
        do {
            try {
                LOG.debug("queue : {}", SchedulerSystem.queue.toString());
                final TaskRunner task = SchedulerSystem.queue.take();
                if (task != null) {
                    LOG.debug("Add to running tasks : {}", task.getTaskName());
                    SchedulerSystem.runningTasks.add(task);
                    task.run();
                    LOG.debug("Remove from running tasks : {}", task.getTaskName());
                    SchedulerSystem.runningTasks.remove(task);
                }
            } catch (final InterruptedException e) {
                return;
            }
        } while (true);
    }

}
