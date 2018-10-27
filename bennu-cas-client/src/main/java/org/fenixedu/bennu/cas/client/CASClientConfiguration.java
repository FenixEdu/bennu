package org.fenixedu.bennu.cas.client;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class CASClientConfiguration {

    @ConfigurationManager(description = "Bennu CAS Client Configuration")
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "cas.enabled", defaultValue = "false", description = "Whether the CAS client is enabled")
        public Boolean casEnabled();

        @ConfigurationProperty(key = "cas.serverUrl", defaultValue = "http://localhost:8080/cas",
                description = "The base URL of the CAS server")
        public String casServerUrl();

        @ConfigurationProperty(key = "cas.serviceUrl", description = "The URL to jump to when no callback is specified")
        public String casServiceUrl();

        @ConfigurationProperty(key = "cas.login.strategy",
                defaultValue = "org.fenixedu.bennu.cas.client.strategy.DefaultTicketValidationStrategy")
        public String getCasLoginStrategy();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
