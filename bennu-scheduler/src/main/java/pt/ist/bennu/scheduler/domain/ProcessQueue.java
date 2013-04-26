package pt.ist.bennu.scheduler.domain;

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
                LOG.info("queue : {}", SchedulerSystem.queue.toString());
                final TaskRunner task = SchedulerSystem.queue.take();
                if (task != null) {
                    task.run();
                }

            } catch (InterruptedException e) {
            }
        } while (true);
    }

}
