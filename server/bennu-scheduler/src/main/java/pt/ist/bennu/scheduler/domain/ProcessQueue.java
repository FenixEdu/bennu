package pt.ist.bennu.scheduler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.TaskRunner;

class ProcessQueue implements Runnable {
    private Logger LOG = LoggerFactory.getLogger(ProcessQueue.class);

    public ProcessQueue() {
        super();
    }

    @Override
    public void run() {
        do {
            try {
                LOG.info("queue : {}", SchedulerSystem.queue.toString());
                final TaskRunner task = SchedulerSystem.queue.take();
                if (task != null) {
                    LOG.info("Add to running tasks : {}", task.getTaskName());
                    SchedulerSystem.runningTasks.add(task);
                    task.run();
                    LOG.info("Remove from running tasks : {}", task.getTaskName());
                    SchedulerSystem.runningTasks.remove(task);
                }

            } catch (InterruptedException e) {
                return;
            }
        } while (true);
    }

}
