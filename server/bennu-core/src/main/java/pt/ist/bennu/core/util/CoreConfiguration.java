package pt.ist.bennu.core.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.core.annotation.ConfigurationManager;
import pt.ist.bennu.core.annotation.ConfigurationProperty;

import com.google.common.base.Strings;

public class CoreConfiguration {
    public static class CasConfig {
        protected CasConfig(Boolean casEnabled, String casServerUrl, String casServiceUrl) {
            this.casEnabled = casEnabled;
            this.casServerUrl = casServerUrl;
            this.casServiceUrl = casServiceUrl;
        }

        private final Boolean casEnabled;

        private final String casServerUrl;

        private final String casServiceUrl;

        public Boolean isCasEnabled() {
            return casEnabled;
        }

        public String getCasServiceUrl() {
            return casServiceUrl;
        }

        public String getCasLoginUrl(HttpServletRequest request) {
            return getCasLoginUrl("https://" + request.getRequestURL());
        }

        public String getCasLoginUrl() {
            return getCasLoginUrl(casServiceUrl);
        }

        public String getCasLoginUrl(String service) {
            return casServerUrl + "/login?service=" + service;
        }

        public String getCasLogoutUrl() {
            return casServerUrl + "/logout";
        }

        public String getCasValidateUrl() {
            return casServerUrl + "/serviceValidate";
        }
    }

    @ConfigurationManager(description = "Bennu Core Configurations")
    public interface ConfigurationProperties {
        @ConfigurationProperty(
                key = "locale.default",
                description = "Default System Locale. If empty falls back to java system default. Must be included in locales.supported")
        public String defaultLocale();

        @ConfigurationProperty(
                key = "default.support.email.address",
                description = "Default email for support. This is intended to be the fall-back for when no application specific email is configured.")
        public String defaultSupportEmailAddress();

        @ConfigurationProperty(
                key = "development.mode",
                description = "Whether development mode is on. Throughout the application the behaviour can change according to this setting.",
                defaultValue = "false")
        public Boolean developmentMode();

        @ConfigurationProperty(key = "locales.supported",
                description = "Locales that should be supported in ResourceBundles and other I18N mechanisms.",
                defaultValue = "en-GB")
        public String supportedLocales();

        @ConfigurationProperty(key = "cas.enabled", defaultValue = "false")
        public Boolean casEnabled();

        @ConfigurationProperty(key = "cas.serverUrl")
        public String casServerUrl();

        @ConfigurationProperty(key = "cas.serviceUrl")
        public String casServiceUrl();
    }

    private static CasConfig casConfig = new CasConfig(getConfiguration().casEnabled(), getConfiguration().casServerUrl(),
            getConfiguration().casServiceUrl());

    private static Set<Locale> supportedLocales = new HashSet<>();

    static {
        for (String locale : getConfiguration().supportedLocales().split("\\s*,\\s*")) {
            supportedLocales.add(Locale.forLanguageTag(locale));
        }
        String defaultLocale = getConfiguration().defaultLocale();
        if (!Strings.isNullOrEmpty(defaultLocale)) {
            Locale.setDefault(Locale.forLanguageTag(defaultLocale));
        }
        if (!supportedLocales().contains(Locale.getDefault())) {
            throw new Error(String.format(
                    "Please make sure the defaultLocale: %s is part of the supported locales in configuration.properties",
                    Locale.getDefault()));
        }
    }

    public static CasConfig casConfig() {
        return casConfig;
    }

    public static Set<Locale> supportedLocales() {
        return supportedLocales;
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
