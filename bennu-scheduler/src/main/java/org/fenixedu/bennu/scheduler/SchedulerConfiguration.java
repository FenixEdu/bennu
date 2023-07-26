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


        @ConfigurationProperty(key = "scheduler.elasticsearch.enabled",
                description = "Use elasticsearch to save logs", defaultValue = "false")
        public Boolean elasticsearchEnabled();


        @ConfigurationProperty(key = "scheduler.elasticsearch.host",
                description = "Elasticsearch host", defaultValue = "localhost")
        public String elasticsearchHost();

        @ConfigurationProperty(key = "scheduler.elasticsearch.port",
                description = "Elasticsearch port", defaultValue = "9200")
        public int elasticsearchPort();

        @ConfigurationProperty(key = "scheduler.elasticsearch.scheme",
                description = "Elasticsearch scheme", defaultValue = "http")
        public String elasticsearchScheme();

        @ConfigurationProperty(key = "scheduler.elasticsearch.username",
                description = "Elasticsearch username", defaultValue = "")
        public String elasticsearchUsername();

        @ConfigurationProperty(key = "scheduler.elasticsearch.password",
                description = "Elasticsearch password", defaultValue = "")
        public String elasticsearchPassword();

        @ConfigurationProperty(key = "scheduler.elasticsearch.search.months",
                description = "Elasticsearch search months", defaultValue = "3")
        public int elasticsearchSearchMonths();


        @ConfigurationProperty(key = "scheduler.elasticsearch.prefix.indexes",
                description = "Elasticsearch prefix indexes", defaultValue = "bennu_")
        public String elasticsearchPrefixIndexes();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
