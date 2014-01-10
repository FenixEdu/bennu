package org.fenixedu.bennu.scheduler;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class SchedulerConfiguration {
    @ConfigurationManager(description = "Scheduler Configuration")
    public interface ConfigurationProperties {
        @ConfigurationProperty(
                key = "scheduler.lease.time.minutes",
                description = "Minutes between attempts to obtain the exclusive execution rights for the scheduler thread."
                        + " In environments with multiple application servers this ensures that one of them has the scheduler running."
                        + " Should be greater than 1.", defaultValue = "5")
        public Integer leaseTimeMinutes();

        @ConfigurationProperty(key = "scheduler.queue.threads.number",
                description = "Number of threads processing the task queue.", defaultValue = "2")
        public Integer queueThreadsNumber();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
