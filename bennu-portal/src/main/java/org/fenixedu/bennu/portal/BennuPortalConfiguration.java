package org.fenixedu.bennu.portal;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class BennuPortalConfiguration {

    @ConfigurationManager(description = "Bennu Portal Configuration")
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "theme.development.mode", defaultValue = "false",
                description = "Disables Theme Caching and allows live-reloading of themes")
        public Boolean themeDevelopmentMode();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
