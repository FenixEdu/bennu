package org.fenixedu.bennu.oauth;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class OAuthProperties {
    @ConfigurationManager(description = "OAuth Properties")
    public interface ConfigurationProperties {
        @ConfigurationProperty(key = "code.timeout.seconds", defaultValue = "60")
        public Integer getCodeExpirationSeconds();

        @ConfigurationProperty(key = "access.token.timeout.seconds", defaultValue = "21600")
        public Integer getAccessTokenExpirationSeconds();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}